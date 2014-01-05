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

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

/**
 * Various functions for loading, parsing and visualizing data.
 *
 * @author Gerrit Meinders
 */
class Tools
{
	private static final int HEX_DUMP_MAX_BYTES_PER_LINE = 10000;

	static void skip( final ByteBuffer buffer, final int count )
	{
		buffer.position( buffer.position() + count );
	}

	/**
	 * Parses a null-terminated string of up to {@code limit} bytes. The final
	 * byte is not required to be a null character.
	 *
	 * @param buffer     Buffer to read from.
	 * @param limit      Maximum string length.
	 * @param charset    Charset to be used.
	 *
	 * @return Parsed string.
	 */
	static String parseCString( final ByteBuffer buffer, final int limit, final Charset charset )
	{
		final byte[] string = new byte[ limit ];
		int length = 0;
		while ( length < limit )
		{
			final byte b = buffer.get();
			if ( b == 0 )
			{
				break;
			}
			else
			{
				string[ length ] = b;
				length++;
			}
		}
		return new String( string, 0, length, charset );
	}

	/**
	 * Parses a null-terminated string of up to {@code length} bytes and skips
	 * any remaining bytes, such that {@code length} bytes are always read.
	 *
	 * @param buffer   Buffer to read from.
	 * @param length   Maximum string length; also the number of bytes to read.
	 * @param charset  Charset to be used.
	 *
	 * @return Parsed string.
	 */
	static String parsePaddedCString( final ByteBuffer buffer, final int length, final Charset charset )
	{
		final byte[] string = new byte[ length ];
		int bytesRead = 0;
		while ( bytesRead < length )
		{
			final byte b = buffer.get();
			if ( b == 0 )
			{
				break;
			}
			else
			{
				string[ bytesRead ] = b;
				bytesRead++;
			}
		}
		final String result = new String( string, 0, bytesRead, charset );
		skip( buffer, length - bytesRead - 1 );
		return result;
	}

	static Date parseTimestamp( final ByteBuffer buffer )
	{
		final double timestamp = buffer.getDouble();
		if ( timestamp == 0.0 )
		{
			return null;
		}

		final int days = (int)timestamp;

		final Calendar calendar = Calendar.getInstance();
		// It appears that 1 January 1900 does not compute.
		calendar.set( 1899, Calendar.DECEMBER, 30, 0, 0, 0 );
		calendar.add( Calendar.DATE, days );

		int millisecond = (int)( 86400000.0 * ( timestamp % 1.0 ) );
		int second = millisecond / 1000;
		millisecond %= 1000;
		int minute = second / 60;
		second %= 60;
		final int hour = minute / 60;
		minute %= 60;

		calendar.set( Calendar.HOUR_OF_DAY, hour );
		calendar.set( Calendar.MINUTE, minute );
		calendar.set( Calendar.SECOND, second );
		calendar.set( Calendar.MILLISECOND, millisecond );
		return calendar.getTime();
	}

	static int parseColor( final ByteBuffer buffer )
	{
		final int rgba = buffer.getInt();
		final int bgra = ( rgba & 0x000000ff ) >> 16 |
		                 ( rgba & 0x00ff0000 ) << 16 |
		                 ( rgba & 0xff00ff00 );
		return bgra;
	}

	static long toLongBE( final byte[] data )
	{
		long result = 0;
		for ( final byte b : data )
		{
			result <<= 8L;
			result |= (long)b & 0xffL;
		}
		return result;
	}

	static long toLongLE( final byte[] data )
	{
		long result = 0;
		for ( int i = data.length - 1; i >= 0; i-- )
		{
			final byte b = data[ i ];
			result <<= 8L;
			result |= (long)b & 0xffL;
		}
		return result;
	}

	static void histogram( final byte[] data )
	{
		histogram( data, 0, data.length );
	}

	static void histogram( final byte[] data, final int offset, final int length )
	{
		final int[] h = new int[256];
		for ( int i = offset; i < offset + length; i++ )
		{
			final byte b = data[ i ];
			h[ (int)b & 0xff ]++;
		}

		System.out.println( "total: " + length );
		for ( int i = 0; i < h.length; i++ )
		{
			System.out.println( i + ":" + h[i] );
		}
	}

	static byte[] loadResource( final Class<?> context, final String name )
	throws IOException
	{
		final InputStream in = context.getResourceAsStream( name );
		if ( in == null )
		{
			throw new FileNotFoundException( name + " (from " + context + ")" );
		}

		try
		{
			return load( in );
		}
		finally
		{
			in.close();
		}
	}

	static byte[] loadResource( final Class<?> context, final String name, final int limit )
	throws IOException
	{
		final InputStream in = context.getResourceAsStream( name );
		if ( in == null )
		{
			throw new FileNotFoundException( name + " (from " + context + ")" );
		}

		try
		{
			return load( in, limit );
		}
		finally
		{
			in.close();
		}
	}

	static byte[] load( final InputStream in )
	throws IOException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte[] buffer = new byte[ 0x1000 ];
		for ( int bytesRead = in.read( buffer ); bytesRead != -1; bytesRead = in.read( buffer ) )
		{
			out.write( buffer, 0, bytesRead );
		}
		return out.toByteArray();
	}

	static byte[] load( final InputStream in, final int limit )
	throws IOException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final BufferedInputStream buffered = new BufferedInputStream( in );
		for ( int i = 0; i < limit; i++ )
		{
			final int b = in.read();
			if ( b == -1 )
			{
				break;
			}
			out.write( b );
		}
		return out.toByteArray();
	}

	static String toHex( final int value )
	{
		final StringBuilder result = new StringBuilder( 8 );
		final int leadingZeroes = Integer.numberOfLeadingZeros( value ) / 4;
		for ( int i = 0; i < leadingZeroes; i++ )
		{
			result.append( '0' );
		}
		result.append( Integer.toHexString( value ) );
		return result.toString();
	}

	static void dumpHex( final PrintStream out, final byte[] data, final int offset, final int length )
	{
		try
		{
			dumpHex( out, new ByteArrayInputStream( data, offset, length ), length );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	public static void dumpHex( final PrintStream out, final InputStream in, final int limit )
	throws IOException
	{
		final StringBuilder buffer = new StringBuilder( HEX_DUMP_MAX_BYTES_PER_LINE );
		for ( int b = in.read(), bytesRead = 0; b != -1 && bytesRead < limit; b = in.read(), bytesRead++ )
		{
			if ( bytesRead > 0 )
			{
				if ( bytesRead % HEX_DUMP_MAX_BYTES_PER_LINE == 0 )
				{
					out.println( buffer );
					buffer.setLength( 0 );
				}
				else
				{
					buffer.append( ' ' );
				}
			}

			if ( b == 0 )
			{
				buffer.append( ". " );
			}
			else
			{
				buffer.append( Character.forDigit( ( b >> 4 ) & 0xf, 16 ) );
				buffer.append( Character.forDigit( b & 0xf, 16 ) );
			}
		}
		out.println( buffer );
	}

	static void dumpAscii( final PrintStream out, final byte[] data, final int offset, final int length )
	{
		final StringBuilder buffer = new StringBuilder( HEX_DUMP_MAX_BYTES_PER_LINE );
		for ( int i = offset; i < offset + length; i++ )
		{
			if ( i > offset )
			{
				if ( ( i - offset ) % HEX_DUMP_MAX_BYTES_PER_LINE == 0 )
				{
					out.println( buffer );
					buffer.setLength( 0 );
				}
				else
				{
					buffer.append( "  " );
				}
			}
			final byte b = data[ i ];
			if ( b >= 0x20 && b <= 0x7f )
			{
				buffer.append( (char)b );
			}
			else
			{
				buffer.append( '.' );
			}
		}
		out.println( buffer );
	}

	public static void ruler( final PrintStream out, final int limit )
	{
		final StringBuilder buffer = new StringBuilder( HEX_DUMP_MAX_BYTES_PER_LINE );
		for ( int i = 0; i < limit; i++ )
		{
			if ( i % 10 == 0 )
			{
				buffer.append( Character.forDigit( ( i / 100 ) % 10, 10 ) );
				buffer.append( Character.forDigit( ( i / 10 ) % 10, 10 ) );
				buffer.append( Character.forDigit( i % 10, 10 ) );
			}
			else
			{
				buffer.append( "   " );
			}
		}
		out.println( buffer );
	}

	public static int indexOf( byte[] haystack, byte[] needle, int offset )
	{
		for ( int i = offset; i < haystack.length - needle.length; i++ )
		{
			boolean match = true;
			for ( int j = 0; j < needle.length; j++ )
			{
				if ( haystack[ i + j ] != needle[ j ] )
				{
					match = false;
					break;
				}
			}
			if ( match )
			{
				return i;
			}
		}
		return -1;
	}

	public static void dump( ByteBuffer buffer, int length )
	{
		final int position = buffer.position();
		final byte[] content = new byte[ length ];
		buffer.get( content );
		ruler( System.out, length );
		dumpHex( System.out, content, 0, length );
		dumpAscii( System.out, content, 0, length );
		buffer.position( position );
	}
}
