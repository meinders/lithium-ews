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

package lithium.io.rtf;

import java.io.*;
import java.util.*;

import lithium.io.*;

/**
 * Writes an RTF document to some implementation-dependent output.
 *
 * @author Gerrit Meinders
 */
public abstract class RtfWriter
	implements RtfVisitor
{

	private final List<Byte> _rtfReservedChars = Arrays.asList( new Byte[] { '\\', '{', '}' } );

	/**
	 * Constructs a new instance.
	 */
	protected RtfWriter()
	{
	}

	public static String writeToString( final RtfGroup document )
	{
		final StringBuilder builder = new StringBuilder();
		document.accept( new StringRtfWriter( builder ) );
		return builder.toString();
	}

	public static byte[] writeToBytes( final RtfGroup document )
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final BinaryRtfWriter writer = new BinaryRtfWriter( out );
		document.accept( writer );
		try
		{
			writer.flush();
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
		return out.toByteArray();
	}

	@Override
	public void visitText( final TextNode text )
	{
		try
		{
			final String escapedText = escapeText( text.getText() );
			write( escapedText );
		}
		catch ( Exception e )
		{
			throw new RuntimeException( e );
		}
	}

	/**
	 * Escapes/decodes special/reserved characters from plain text to RTF ready text.
	 */
	private String escapeText( final String text )
	throws UnsupportedEncodingException
	{
		final StringBuilder result = new StringBuilder();

		final byte[] bytes = text.getBytes( Config.charset );
		for ( final byte c : bytes )
		{
			if ( c >= 0 )
			{
				if ( _rtfReservedChars.contains( c ) )
				{
					result.append( '\\' );
				}
				result.append( (char)c );
			}
			else
			{
				// Special character
				final String hexValue = Integer.toHexString( c ).substring( 6 );
				result.append( '\\' );
				result.append( '\'' );
				result.append( hexValue );
			}
		}
		return result.toString();
	}

	@Override
	public void visitControlWord( final ControlWord controlWord )
	{
		try
		{
			write( '\\' );
			write( controlWord.getWord() );

			if ( controlWord.getNumericParameter() != null )
			{
				write( controlWord.getNumericParameter().toString() );
			}

			if ( controlWord.isDelimitedBySpace() )
			{
				write( ' ' );
			}
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	@Override
	public void visitControlSymbol( final ControlSymbol controlSymbol )
	{
		try
		{
			write( '\\' );
			write( controlSymbol.getSymbol() );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	@Override
	public VisitResult groupStart( final RtfGroup group )
	{
		try
		{
			write( '{' );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
		return VisitResult.CONTINUE;
	}

	@Override
	public void groupEnd( final RtfGroup group )
	{try
		{
			write( '}' );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	public abstract void write( char c )
		throws IOException;

	public abstract void write( String s )
		throws IOException;
}
