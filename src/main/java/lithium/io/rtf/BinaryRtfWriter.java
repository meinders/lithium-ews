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

import lithium.io.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Writes an RTF document to a binary stream.
 *
 * @author Gerrit Meinders
 */
class BinaryRtfWriter
	extends RtfWriter
{
	private final VariableCharsetWriter _writer;

	/**
	 * Constructs a new instance.
	 */
	public BinaryRtfWriter( final OutputStream stream )
	{
		final Charset charset = Charset.forName(Config.charset);
		_writer = new VariableCharsetWriter( stream, charset );
	}

	@Override
	public void visitControlWord( final ControlWord controlWord )
	{
		try
		{
			if ( "ansi".equals( controlWord.getWord() ) )
			{
				_writer.setCharset( Charset.forName(Config.charset) );
			}
			else if ( "mac".equals( controlWord.getWord() ) || "pc".equals( controlWord.getWord() ) || "pca".equals( controlWord.getWord() ) )
			{
				System.err.println( "WARNING: Found code page '" + controlWord.getWord() + "', which is not supported." );
			}
			else if ( "cpg".equals( controlWord.getWord() ) )
			{
				System.err.println( "WARNING: Found 'cpg' control word (for code page '" + controlWord.getNumericParameter() + "'), which is not supported." );
			}
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}

		super.visitControlWord( controlWord );
	}

	@Override
	public void write( final char c )
		throws IOException
	{
		_writer.write( (int)c );
	}

	@Override
	public void write( final String s )
		throws IOException
	{
		_writer.write( s );
	}

	public void flush()
		throws IOException
	{
		_writer.flush();
	}
}
