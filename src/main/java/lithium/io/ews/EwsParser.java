/*
 * Copyright 2013 Gerrit Meinders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lithium.io.ews;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.zip.*;

import static lithium.io.ews.Tools.*;

/**
 * Reads schedules stored in the EWS file format.
 *
 * @author Gerrit Meinders
 */
public class EwsParser
{
	private Charset _charset = Charset.defaultCharset();

	private int _index = 1;

	public EwsParser()
	{
	}

	public Charset getCharset()
	{
		return _charset;
	}

	public void setCharset( final Charset charset )
	{
		_charset = charset;
	}

	public Schedule parse( final ByteBuffer buffer )
	throws IOException
	{
		buffer.order( ByteOrder.LITTLE_ENDIAN );

		buffer.position( 56 );
		final int playlistEntryCount = buffer.getInt();
		final int playlistEntryLength = (int)buffer.getShort();

		final Schedule playlist = new Schedule();
		final List<ScheduleEntry> playlistEntries = playlist.getEntries();

		for ( int i = 0; i < playlistEntryCount; i++ )
		{
			final ScheduleEntry entry = parsePlaylistEntry( buffer, playlistEntryLength );
			playlistEntries.add( entry );
		}

		return playlist;
	}

	private ScheduleEntry parsePlaylistEntry( final ByteBuffer buffer, final int size )
	throws IOException
	{
		final int start = buffer.position();

		final String title = parsePaddedCString( buffer, 51, getCharset() );
		final String mediaResource = parsePaddedCString( buffer, 256, getCharset() );
		final String author = parsePaddedCString( buffer, 51, getCharset() );
		final String copyright = parsePaddedCString( buffer, 101, getCharset() );
		final String administrator = parsePaddedCString( buffer, 51, getCharset() );

		buffer.position( start + 792 );
		final Date timestamp = parseTimestamp( buffer );

		final int contentPointer = buffer.getInt();

		buffer.position( start + 820 );
		final ScheduleEntry.Type type = parseScheduleEntryType( buffer );
		skip( buffer, 12 );

		/*final int isPresentation = */buffer.getInt();

		final int presentationLength = buffer.getInt();

		buffer.position( start + 1155 );
		final String notes = parsePaddedCString( buffer, 161, getCharset() );
		skip( buffer, 94 );
		final String songNumber = parsePaddedCString( buffer, 11, getCharset() );

		buffer.position( start + 1480 );
		final int originalResourceLength = buffer.getInt();

		final ScheduleEntry result = new ScheduleEntry();
		result.setTitle( title );
		if ( !mediaResource.isEmpty() )
		{
			result.setMediaResource( mediaResource );
		}
		if ( !author.isEmpty() )
		{
			result.setAuthor( author );
		}
		if ( !copyright.isEmpty() )
		{
			result.setCopyright( copyright );
		}
		if ( !administrator.isEmpty() )
		{
			result.setAdministrator( administrator );
		}
		result.setTimestamp( timestamp );
		result.setType( type );
		result.setNotes( notes );
		result.setSongNumber( songNumber );

		final Content content;
		buffer.position( contentPointer );

		if ( ( type == ScheduleEntry.Type.SONG ) ||
		     ( type == ScheduleEntry.Type.SCRIPTURE ) )
		{
			content = parseDeflatedTextContent( buffer );
		}
		else if ( ( type == ScheduleEntry.Type.PRESENTATION ) ||
		          ( type == ScheduleEntry.Type.VIDEO ) ||
		          ( type == ScheduleEntry.Type.LIVE_VIDEO ) ||
		          ( type == ScheduleEntry.Type.IMAGE ) ||
		          ( type == ScheduleEntry.Type.AUDIO ) ||
		          ( type == ScheduleEntry.Type.WEB ) )
		{
			content = parseBinaryContent( type, buffer );
		}
		else
		{
			content = null;
		}

		result.setContent( content );

		if ( originalResourceLength > 0 )
		{
			if ( presentationLength > 0 )
			{
				final ByteBuffer presentationBuffer = buffer.slice();
				presentationBuffer.order( ByteOrder.LITTLE_ENDIAN );
				presentationBuffer.limit( presentationLength );
				final Presentation presentation = parsePresentation( presentationBuffer );
				result.setPresentation( presentation );
			}
			else
			{
				final int embeddedResourceLength = buffer.getInt();
				if ( embeddedResourceLength > 0 )
				{
					if ( type == ScheduleEntry.Type.VIDEO )
					{
						final int unknown = buffer.getInt();
						if ( unknown != 0 )
						{
							System.err.println( "Unexpected embedded video content. Expected 0, but was " + unknown );
						}
					}

					final byte[] embeddedResource = new byte[ embeddedResourceLength ];
					buffer.get( embeddedResource );
					final BinaryContent binaryContent = new BinaryContent();
					binaryContent.setBytes( embeddedResource );
					result.setEmbeddedContent( binaryContent );
				}
			}
		}

		buffer.position( start + size );
		return result;
	}

	private Presentation parsePresentation( final ByteBuffer buffer )
	{
		final int headerLength = buffer.getInt();

		final String identifier = parsePaddedCString( buffer, 16, getCharset() );
		if ( !"$ezwppstream$".equals( identifier ) )
		{
			System.err.println( "Unexpected value in presentation content: " + identifier );
		}

		final byte[] unknown = new byte[ headerLength - 20 ];
		buffer.get( unknown );

		final Presentation presentation = new Presentation();
		presentation.setUnknown( unknown );

		final int slideCount = buffer.getInt();
		for ( int slideIndex = 0; slideIndex < slideCount; slideIndex++ )
		{
			final Slide slide = parseSlide( buffer );
			presentation.addSlide( slide );
		}

		return presentation;
	}

	private Slide parseSlide( final ByteBuffer buffer )
	{
		final int contentPointer = buffer.getInt();

		final byte[] slideUnknown = new byte[ 12 ];
		buffer.get( slideUnknown );

		final int position = buffer.position();
		buffer.position( contentPointer );

		buffer.get(); // 1

		final int contentLength = buffer.getInt();
		final byte[] content = new byte[ contentLength ];
		buffer.get( content );

		buffer.position( position );

		final Slide slide = new Slide();
		slide.setUnknown( slideUnknown );
		slide.setContent( content );
		return slide;
	}

	private ScheduleEntry.Type parseScheduleEntryType( final ByteBuffer buffer )
	{
		ScheduleEntry.Type result = ScheduleEntry.Type.UNKNOWN;
		switch ( buffer.getInt() )
		{
			case 1:
				result = ScheduleEntry.Type.SONG;
				break;
			case 2:
				result = ScheduleEntry.Type.SCRIPTURE;
				break;
			case 3:
				result = ScheduleEntry.Type.PRESENTATION;
				break;
			case 4:
				result = ScheduleEntry.Type.VIDEO;
				break;
			case 5:
				result = ScheduleEntry.Type.LIVE_VIDEO;
				break;
			case 7:
				result = ScheduleEntry.Type.IMAGE;
				break;
			case 8:
				result = ScheduleEntry.Type.AUDIO;
				break;
			case 9:
				result = ScheduleEntry.Type.WEB;
				break;
		}
		return result;
	}

	private Content parseBinaryContent( ScheduleEntry.Type type, final ByteBuffer buffer )
	{
		final Content result;
		final int contentLength = buffer.getInt();
		if ( contentLength < 0 )
		{
			throw new IllegalArgumentException( "contentLength: " + contentLength );
		}

		/*
		 * Some types have 4 additional zero bytes. Don't know why.
		 */
		if ( type == ScheduleEntry.Type.PRESENTATION )
		{
			final int unknown = buffer.getInt();
			if ( unknown != 0 )
			{
				System.err.println( "Unexpected embedded video content. Expected 0, but was " + unknown );
			}
		}

		// Uncompressed content.
		final byte[] content = new byte[ contentLength ];
		buffer.get( content );
		final BinaryContent binaryContent = new BinaryContent();
		binaryContent.setBytes( content );
		result = binaryContent;
		return result;
	}

	private TextContent parseDeflatedTextContent( final ByteBuffer buffer )
	throws IOException
	{
		final int contentLength = buffer.getInt();
		if ( contentLength < 0 )
		{
			throw new IllegalArgumentException( "contentLength: " + contentLength );
		}

		final byte[] compressedContent = new byte[ contentLength - 10 ];
		buffer.get( compressedContent );

		buffer.order( ByteOrder.BIG_ENDIAN );
		buffer.position( buffer.position() - 4 );
		final int expectedChecksum = buffer.getInt();
		buffer.order( ByteOrder.LITTLE_ENDIAN );

		buffer.position( buffer.position() + 4 );

		final int decompressedLength = buffer.getInt();
		if ( decompressedLength < 0 )
		{
			throw new IllegalArgumentException( "decompressedLength: " + decompressedLength );
		}

		final ByteArrayInputStream in = new ByteArrayInputStream( compressedContent );
		final InflaterInputStream inflated = new InflaterInputStream( in );
		final CheckedInputStream checked = new CheckedInputStream( inflated, new Adler32() );

		final InputStreamReader reader = new InputStreamReader( checked, getCharset() );
		final StringBuilder content = new StringBuilder( decompressedLength );
		for ( int c = reader.read(); c != -1; c = reader.read() )
		{
			content.append( (char)c );
		}

		final Checksum actualChecksum = checked.getChecksum();
		if ( expectedChecksum != (int)actualChecksum.getValue() )
		{
			System.err.println( "WARNING: Checksum error. Expected " + toHex( expectedChecksum ) + ", but was " + toHex( (int)actualChecksum.getValue() ) );
		}

		final TextContent result = new TextContent();
		result.setText( content.toString() );
		return result;
	}
}
