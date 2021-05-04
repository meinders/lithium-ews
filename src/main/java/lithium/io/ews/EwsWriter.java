/*
 * Copyright 2013-2014 Gerrit Meinders
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

import lithium.io.rtf.RtfWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

/**
 * Writes schedules in the EWS file format.
 *
 * @author Gerrit Meinders
 */
public class EwsWriter
{
	private static final int SCHEDULE_ENTRY_LENGTH = 1816;

	private final OutputStream _out;

	private Charset _charset = Charset.defaultCharset();

	public EwsWriter( final OutputStream out )
	{
		_out = out;
	}

	public EwsWriter( final File file ) throws IOException {
		file.createNewFile();
		_out = new FileOutputStream(file);
	}

	public Charset getCharset()
	{
		return _charset;
	}

	public void setCharset( final Charset charset )
	{
		_charset = charset;
	}

	public void write( final Schedule schedule )
	throws IOException
	{
		writeImpl( schedule );
	}

	private void writeImpl( final Schedule schedule )
	throws IOException
	{
		final List<ScheduleEntry> entries = schedule.getEntries();

		writeString( "EasyWorship Schedule File Version    5" );
		writeInt( 0x00001a00 );
		writeInt( 0 );
		writeInt( 0x00004014 );
		writeInt( 0 );
		writeShort( 0x4014 );
		writeInt( entries.size() );
		writeShort( SCHEDULE_ENTRY_LENGTH );

		final int scheduleLength = entries.size() * SCHEDULE_ENTRY_LENGTH;
		int cumulativeContentLength = 0;

		for ( final ScheduleEntry entry : entries )
		{
			// Song information
			writePaddedString( entry.getTitle(), 51 );
			writeZeroes( 256 );
			writePaddedString( entry.getAuthor(), 51 );
			writePaddedString( entry.getCopyright(), 101 );
			writePaddedString( entry.getAdministrator(), 51 );

			// Unknown fields.
			writeInt( 0x101 );
			writeInt( 0 );
			writeInt( 0x80 );
			writeInt( 0x80 );
			writeInt( 0x20000 );
			writeZeroes( 262 );

			writeTimestamp( entry.getTimestamp() );
			final int contentPointer = 62 + scheduleLength + cumulativeContentLength;
			writeInt( contentPointer );

			// More unknown fields.
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0x1 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0x100 );
			writeInt( 0 );
			writeInt( 0x1 );
			writeZeroes( 252 );
			writeInt( 0x1 );
			writeInt( 0 );
			writeInt( 0x1 );
			writeInt( 0 );
			writeInt( 0x1 );
			writeInt( 0 );
			_out.write( 0x2 );
			_out.write( 0x2 );
			_out.write( 0x2 );
			_out.write( 0x2 );
			_out.write( 0x3 );
			_out.write( 0x3 );
			_out.write( 0x1 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0 );
			writeInt( 0 );

			// More song information
			writePaddedString( entry.getNotes(), 161 );
			writeZeroes( 94 );
			writePaddedString( entry.getSongNumber(), 11 );
			writeZeroes( 99 );
			writeInt( 0x2 );
			writeZeroes( 292 );

			final Content content = entry.getContent();
			if ( content instanceof TextContent )
			{
				final TextContent textContent = (TextContent)content;
				final byte[] bytes = RtfWriter.writeToBytes( textContent.getText() );

				final ByteArrayOutputStream compressedOut = new ByteArrayOutputStream();
				final DeflaterOutputStream deflaterOut = new DeflaterOutputStream( compressedOut );
				deflaterOut.write( bytes );
				deflaterOut.close();
				cumulativeContentLength += compressedOut.size() + 14;
			}
			else if ( content instanceof BinaryContent )
			{
				final BinaryContent binaryContent = (BinaryContent)content;
				final byte[] bytes = binaryContent.getBytes();
				cumulativeContentLength += bytes.length + 4;
			}
			else if ( content != null )
			{
				throw new IllegalArgumentException( "Unsupported content: " + content );
			}
		}

		for ( final ScheduleEntry entry : entries )
		{
			final Content content = entry.getContent();
			if ( content instanceof TextContent )
			{
				final TextContent textContent = (TextContent)content;
				final byte[] bytes = RtfWriter.writeToBytes( textContent.getText() );

				final ByteArrayOutputStream compressedOut = new ByteArrayOutputStream();
				final DeflaterOutputStream deflaterOut = new DeflaterOutputStream( compressedOut );
				deflaterOut.write( bytes );
				deflaterOut.close();

				final byte[] compressedContent = compressedOut.toByteArray();
				writeInt( compressedContent.length + 10 );
				_out.write( compressedContent );
				_out.write( 0x51 );
				_out.write( 0x4b );
				_out.write( 0x03 );
				_out.write( 0x04 );
				writeInt( bytes.length );
				_out.write( 0x08 );
				_out.write( 0x0 );
			}
			else if ( content instanceof BinaryContent )
			{
				final BinaryContent binaryContent = (BinaryContent)content;
				final byte[] bytes = binaryContent.getBytes();
				cumulativeContentLength += bytes.length + 4;
			}
			else if ( content != null )
			{
				throw new IllegalArgumentException( "Unsupported content: " + content );
			}

		}
	}

	private void writeZeroes( final int count )
	throws IOException
	{
		for ( int i = 0; i < count; i++ )
		{
			_out.write( 0 );
		}
	}

	private void writeString( final String string )
	throws IOException
	{
		_out.write( string.getBytes( getCharset() ) );
	}

	private void writeTimestamp( final Date date )
	throws IOException
	{
		if ( date == null )
		{
			writeDouble( 0.0 );
		}
		else
		{
			final Calendar calendar = Calendar.getInstance();

			calendar.setTime( date );
			calendar.set( Calendar.HOUR_OF_DAY, 0 );
			calendar.set( Calendar.MINUTE, 0 );
			calendar.set( Calendar.SECOND, 0 );
			calendar.set( Calendar.MILLISECOND, 0 );
			final Date startOfDay = calendar.getTime();

			// It appears that 1 January 1900 does not compute.
			calendar.set( 1899, Calendar.DECEMBER, 30, 0, 0, 0 );
			final Date epoch = calendar.getTime();

			// TODO: Not accurate for various reasons. Use Joda-Time.
			final long dateDifference = date.getTime() - epoch.getTime();
			final long timeDifference = date.getTime() - startOfDay.getTime();
			final double timestamp = (double)( dateDifference / 86400000L ) + (double)timeDifference / 86400000.0;

			writeDouble( timestamp );
		}
	}

	private void writeDouble( final double d )
	throws IOException
	{
		writeLong( Double.doubleToLongBits( d ) );
	}

	private void writeLong( final long i )
	throws IOException
	{
		writeInt( (int)i );
		writeInt( (int)( i >> 32 ) );
	}

	private void writeInt( final int i )
	throws IOException
	{
		_out.write( i & 0xff );
		_out.write( ( i >> 8 ) & 0xff );
		_out.write( ( i >> 16 ) & 0xff );
		_out.write( ( i >> 24 ) & 0xff );
	}

	private void writeShort( final int i )
	throws IOException
	{
		_out.write( i & 0xff );
		_out.write( ( i >> 8 ) & 0xff );
	}

	private void writePaddedString( final String string, final int length )
	throws IOException
	{
		if ( string == null )
		{
			writeZeroes( length );
		}
		else
		{
			final byte[] bytes = string.getBytes( getCharset() );
			_out.write( bytes );
			writeZeroes( length - bytes.length );
		}
	}

	public void close()
	throws IOException
	{
		_out.close();
	}
}
