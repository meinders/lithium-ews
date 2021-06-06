/*
 * Copyright 2013-2021 Gerrit Meinders
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
import java.nio.charset.*;

import lithium.io.*;

/**
 * Writes an RTF document to a binary stream.
 *
 * @author Gerrit Meinders
 */
public class BinaryRtfWriter
extends RtfWriter
{
	/**
	 * Underlying writer.
	 */
	private final VariableCharsetWriter _writer;

	/**
	 * Constructs a new instance.
	 *
	 * @param stream Stream to write to.
	 */
	public BinaryRtfWriter( final OutputStream stream )
	{
		final Charset charset = Charset.forName( Config.charset );
		_writer = new VariableCharsetWriter( stream, charset );
	}

	@Override
	public void visitControlWord( final ControlWord controlWord )
	{
		try
		{
			String charsetName = null;
			if ( "ansi".equals( controlWord.getWord() ) )
			{
				charsetName = Config.charset;
			}
			else if ( "mac".equals( controlWord.getWord() ) )
			{
				// TODO: Which (legacy) Mac code page does this indicate? MacRoman?
				System.err.println( "WARNING: Found code page '" + controlWord.getWord() + "', which is not supported." );
			}
			else if ( "pc".equals( controlWord.getWord() ) )
			{
				charsetName = "Cp437";
			}
			else if ( "pca".equals( controlWord.getWord() ) )
			{
				charsetName = "Cp850";
			}
			else if ( "ansicpg".equals( controlWord.getWord() ) || // inside header section
			          "cpg".equals( controlWord.getWord() ) ) // inside font table group
			{
				//noinspection InjectedReferences
				charsetName = "Cp" + controlWord.getNumericParameter();
			}

			if ( charsetName != null )
			{
				try
				{
					_writer.setCharset( Charset.forName( charsetName ) );
				}
				catch ( final UnsupportedCharsetException e )
				{
					System.err.println( "WARNING: Unsupported character set '" + charsetName + "' (for '" + controlWord.getWord() + ( controlWord.getNumericParameter() == null ? "" : String.valueOf( controlWord.getNumericParameter() ) ) + "' control word)" );
				}
			}
		}
		catch ( final IOException e )
		{
			throw new RuntimeException( e );
		}

		super.visitControlWord( controlWord );
	}

	@Override
	public void write( final char c )
	throws IOException
	{
		_writer.write( c );
	}

	@Override
	public void write( final String s )
	throws IOException
	{
		_writer.write( s );
	}

	/**
	 * Flushes the underlying writer.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	public void flush()
	throws IOException
	{
		_writer.flush();
	}
}
