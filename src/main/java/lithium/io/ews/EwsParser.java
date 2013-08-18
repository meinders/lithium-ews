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

import java.awt.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;
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

		final String scheduleFormatIdentifier = parsePaddedCString( buffer, 34, getCharset() );
		if ( !scheduleFormatIdentifier.startsWith( "EasyWorship Schedule File" ) )
		{
			throw new IllegalArgumentException( "Not an EasyWorship schedule file." );
		}

		final String scheduleFormatVersionString = parsePaddedCString( buffer, 5, getCharset() ).trim();

		if ( "5".equals( scheduleFormatVersionString ) )
		{
			skip( buffer, 19 );
		}
		else if ( "3".equals( scheduleFormatVersionString ) )
		{
			skip( buffer, 11 );
		}
		else if ( "1.6".equals( scheduleFormatVersionString ) )
		{
			skip( buffer, 3 );
		}
		else
		{
			throw new IllegalArgumentException( "Unsupported version: '" + scheduleFormatVersionString + "'" );
		}

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

//		System.out.println( "Background (510..535), containing unknowns (510, 530..535)" );
//		dump( buffer, 26 );
		skip( buffer, 1 );
		final boolean defaultBackground = ( buffer.get() != 0 );
		final ScheduleEntry.BackgroundType backgroundType = parseBackgroundType( buffer.getInt() );
		final Color backgroundColor = new Color( parseColor( buffer ), false );
		final Color gradientColor1 = new Color( parseColor( buffer ), false );
		final Color gradientColor2 = new Color( parseColor( buffer ), false );
		final ScheduleEntry.GradientStyle gradientStyle = parseGradientStyle( buffer.get() );
		final ScheduleEntry.GradientVariant gradientVariant = parseGradientVariant( buffer.get() );
		skip( buffer, 6 );

		final String backgroundName = parsePaddedCString( buffer, 256, getCharset() );

		final Date timestamp = parseTimestamp( buffer );

		final int contentPointer = buffer.getInt();

//		System.out.println( "Unknown (804)" );
//		dump( buffer, 16 );
		skip( buffer, 16 );

		final ScheduleEntry.Type type = parseScheduleEntryType( buffer );

//		System.out.println( "Unknown (824)" );
//		dump( buffer, 16 );
		skip( buffer, 16 );

		final int presentationLength = buffer.getInt();

//		System.out.println( "Font settings (844..851), containing unknowns (846..847)" );
//		dump( buffer, 8 );
		final boolean customFontSettings = ( buffer.get() != 0 );
		final boolean fontSizeAutomatic = ( buffer.get() != 0 );
		skip( buffer, 2 );

		String notes = null;
		String songNumber = null;
		int mediaContentPointer = 0;
		ScheduleEntry.AspectRatio aspectRatio = null;
		int originalResourceLength = 0;

		if ( size > 848 )
		{
			final int fontSize = buffer.getInt();

			final boolean useDefaultFont = ( buffer.get() != 0 );
			final String fontName = parsePaddedCString( buffer, 255, getCharset() );

			final boolean foregroundAutomatic = buffer.getInt() == 1;
			final Color foregroundColor = new Color( parseColor( buffer ), false );
			final boolean shadowAutomatic = buffer.getInt() == 1;
			final Color shadowColor = new Color( parseColor( buffer ), false );
			final boolean outlineAutomatic = buffer.getInt() == 1;
			final Color outlineColor = new Color( parseColor( buffer ), false );

			final Boolean shadowEnabled = parseTristate( buffer.get() );
			final Boolean outlineEnabled = parseTristate( buffer.get() );
			final Boolean boldEnabled = parseTristate( buffer.get() );
			final Boolean italicEnabled = parseTristate( buffer.get() );
			final ScheduleEntry.HorizontalAlignment horizontalTextAlignment = parseHorizontalAlignment( buffer.get() );
			final ScheduleEntry.VerticalAlignment verticalTextAlignment = parseVerticalAlignment( buffer.get() );

			final boolean defaultTextMargins = ( buffer.get() != 0 );
			final int textMarginLeft = buffer.getInt();
			final int textMarginTop = buffer.getInt();
			final int textMarginRight = buffer.getInt();
			final int textMarginBottom = buffer.getInt();

			notes = parsePaddedCString( buffer, 161, getCharset() );
//			System.out.println( "Unknown (1316)" );
//			dump( buffer, 94 );
			skip( buffer, 94 );
			songNumber = parsePaddedCString( buffer, 11, getCharset() );

//			System.out.println( "Unknown (1421)" );
//			dump( buffer, 59 );
			skip( buffer, 59 );

			originalResourceLength = buffer.getInt();
//			System.out.println( "Unknown (1484)" );
//			dump( buffer, 12 );
			skip( buffer, 12 );
			mediaContentPointer = buffer.getInt();
//			System.out.println( "Unknown (1500)" );
//			dump( buffer, 20 );
			skip( buffer, 20 );

			aspectRatio = parseAspectRatio( buffer.getInt() );

//			System.out.println( "Unknown (1524)" );
//			dump( buffer, 292 );
			skip( buffer, 292 );
		}

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

		buffer.position( contentPointer );

		if ( ( type == ScheduleEntry.Type.SONG ) ||
		     ( type == ScheduleEntry.Type.SCRIPTURE ) )
		{
			final TextContent content = parseDeflatedTextContent( buffer );
			result.setContent( content );
		}
		else if ( type == ScheduleEntry.Type.VIDEO )
		{
			final BinaryContent content = parseBinaryContent( ScheduleEntry.Type.IMAGE, buffer );
			result.setThumbnailImage( content );

			if ( mediaContentPointer > 0 )
			{
				final int position = buffer.position();
				buffer.position( mediaContentPointer );
				final BinaryContent mediaContent = parseBinaryContent( type, buffer );
				result.setContent( mediaContent );
				buffer.position( position );
			}
		}
		else if ( ( type == ScheduleEntry.Type.PRESENTATION ) ||
		          ( type == ScheduleEntry.Type.LIVE_VIDEO ) ||
		          ( type == ScheduleEntry.Type.IMAGE ) ||
		          ( type == ScheduleEntry.Type.AUDIO ) ||
		          ( type == ScheduleEntry.Type.WEB ) )
		{
			final BinaryContent content = parseBinaryContent( type, buffer );
			result.setContent( content );
		}

		if ( !defaultBackground )
		{
			final Background background;

			if ( backgroundType == ScheduleEntry.BackgroundType.COLOR )
			{
				final ColorBackground colorBackground = new ColorBackground();
				background = colorBackground;
				colorBackground.setColor( backgroundColor );
			}
			else if ( backgroundType == ScheduleEntry.BackgroundType.GRADIENT )
			{
				final GradientBackground gradientBackground = new GradientBackground();
				background = gradientBackground;
				gradientBackground.setColor1( gradientColor1 );
				gradientBackground.setColor2( gradientColor2 );
				gradientBackground.setStyle( gradientStyle );
				gradientBackground.setVariant( gradientVariant );
			}
			else if ( backgroundType == ScheduleEntry.BackgroundType.IMAGE_TILED || backgroundType == ScheduleEntry.BackgroundType.IMAGE_SCALED )
			{
				final ImageBackground imageBackground = new ImageBackground();
				background = imageBackground;
				imageBackground.setName( backgroundName );

				if ( !backgroundName.isEmpty() )
				{
					final BinaryContent backgroundImage = parseBinaryContent( ScheduleEntry.Type.IMAGE, buffer );
					imageBackground.setImage( backgroundImage );
				}

				imageBackground.setTiled( backgroundType == ScheduleEntry.BackgroundType.IMAGE_TILED );

				if ( backgroundType == ScheduleEntry.BackgroundType.IMAGE_SCALED )
				{
					imageBackground.setAspectRatio( aspectRatio );
				}
			}
			else if ( backgroundType == ScheduleEntry.BackgroundType.VIDEO )
			{
				final VideoBackground videoBackground = new VideoBackground();
				background = videoBackground;
				videoBackground.setName( backgroundName );

				if ( !backgroundName.isEmpty() )
				{
					final BinaryContent previewImage = parseBinaryContent( ScheduleEntry.Type.IMAGE, buffer );
					videoBackground.setImage( previewImage );

					if ( mediaContentPointer > 0 )
					{
						final int position = buffer.position();
						buffer.position( mediaContentPointer );
						final BinaryContent video = parseBinaryContent( ScheduleEntry.Type.VIDEO, buffer );
						videoBackground.setVideo( video );
						buffer.position( position );
					}
				}

				if ( backgroundType == ScheduleEntry.BackgroundType.IMAGE_SCALED )
				{
					videoBackground.setAspectRatio( aspectRatio );
				}
			}
			else if ( backgroundType == ScheduleEntry.BackgroundType.LIVE_VIDEO )
			{
				final LiveVideoBackground liveVideoBackground = new LiveVideoBackground();
				background = liveVideoBackground;
				liveVideoBackground.setName( backgroundName );

				if ( backgroundType == ScheduleEntry.BackgroundType.IMAGE_SCALED )
				{
					liveVideoBackground.setAspectRatio( aspectRatio );
				}
			}
			else
			{
				throw new IllegalArgumentException( "Unsupported background type: " + backgroundType );
			}

			result.setBackground( background );
		}

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

	private BinaryContent parseBinaryContent( final ScheduleEntry.Type type, final ByteBuffer buffer )
	{
		final int contentLength = buffer.getInt();
		if ( contentLength < 0 )
		{
			throw new IllegalArgumentException( "contentLength: " + contentLength );
		}

		/*
		 * Some types have 4 additional zero bytes. Don't know why.
		 */
		if ( type == ScheduleEntry.Type.VIDEO ||
		     type == ScheduleEntry.Type.PRESENTATION )
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

		final BinaryContent result = new BinaryContent();
		result.setBytes( content );
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

//		System.out.println( "Unknown text content field:" );
//		dump( buffer, 4 );
		skip( buffer, 4 );

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

//		System.out.println( "Unknown text content field:" );
//		dump( buffer, 2 );
		skip( buffer, 2 );

		final TextContent result = new TextContent();
		result.setText( content.toString() );
		return result;
	}

	private ScheduleEntry.AspectRatio parseAspectRatio( int i )
	{
		switch ( i )
		{
			case 0:
				return null;
			case 1:
				return ScheduleEntry.AspectRatio.MAINTAIN;
			case 2:
				return ScheduleEntry.AspectRatio.STRETCH;
			case 3:
				return ScheduleEntry.AspectRatio.ZOOM;
			default:
				System.err.println( "Unsupported aspect ratio: " + i + " (using default instead)" );
				return null;
		}
	}

	private ScheduleEntry.HorizontalAlignment parseHorizontalAlignment( byte b )
	{
		switch ( b )
		{
			case 0:
				return ScheduleEntry.HorizontalAlignment.LEFT;
			case 1:
				return ScheduleEntry.HorizontalAlignment.CENTER;
			case 2:
				return ScheduleEntry.HorizontalAlignment.RIGHT;
			case 3:
				return null;
			default:
				throw new IllegalArgumentException( String.valueOf( b ) );
		}
	}

	private ScheduleEntry.VerticalAlignment parseVerticalAlignment( byte b )
	{
		switch ( b )
		{
			case 0:
				return ScheduleEntry.VerticalAlignment.TOP;
			case 1:
				return ScheduleEntry.VerticalAlignment.CENTER;
			case 2:
				return ScheduleEntry.VerticalAlignment.BOTTOM;
			case 3:
				return null;
			default:
				throw new IllegalArgumentException( String.valueOf( b ) );
		}
	}

	private Boolean parseTristate( final byte b )
	{
		switch ( b )
		{
			case 0:
				return Boolean.FALSE;
			case 1:
				return Boolean.TRUE;
			case 2:
				return null;
			default:
				throw new IllegalArgumentException( String.valueOf( b ) );
		}
	}

	private ScheduleEntry.BackgroundType parseBackgroundType( int i )
	{
		switch ( i )
		{
			case 0:
				return ScheduleEntry.BackgroundType.COLOR;
			case 1:
				return ScheduleEntry.BackgroundType.GRADIENT;
			case 2:
				return ScheduleEntry.BackgroundType.IMAGE_TILED;
			case 3:
				return ScheduleEntry.BackgroundType.IMAGE_SCALED;
			case 4:
				return ScheduleEntry.BackgroundType.VIDEO;
			case 5:
				return ScheduleEntry.BackgroundType.LIVE_VIDEO;
			default:
				throw new IllegalArgumentException( String.valueOf( i ) );
		}
	}

	private ScheduleEntry.GradientStyle parseGradientStyle( byte b )
	{
		switch ( b )
		{
			case 0:
				return ScheduleEntry.GradientStyle.HORIZONTAL;
			case 1:
				return ScheduleEntry.GradientStyle.VERTICAL;
			case 2:
				return ScheduleEntry.GradientStyle.DIAGONAL_UP;
			case 3:
				return ScheduleEntry.GradientStyle.DIAGONAL_DOWN;
			default:
				throw new IllegalArgumentException( String.valueOf( b ) );
		}
	}

	private ScheduleEntry.GradientVariant parseGradientVariant( byte b )
	{
		switch ( b )
		{
			case 0:
				return ScheduleEntry.GradientVariant.LINEAR;
			case 1:
				return ScheduleEntry.GradientVariant.LINEAR_REVERSED;
			case 2:
				return ScheduleEntry.GradientVariant.BILINEAR;
			case 3:
				return ScheduleEntry.GradientVariant.BILINEAR_REVERSED;
			default:
				throw new IllegalArgumentException( String.valueOf( b ) );
		}
	}
}
