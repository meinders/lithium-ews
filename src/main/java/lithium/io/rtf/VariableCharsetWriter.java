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
import java.nio.charset.*;

/**
 * Writer that supports changing between charsets while writing to an underlying
 * output stream.
 *
 * @author Gerrit Meinders
 */
class VariableCharsetWriter
	extends Writer
{
	private final OutputStream _stream;

	private OutputStreamWriter _writer;

	public VariableCharsetWriter( final OutputStream outputStream, final Charset initialCharset )
	{
		_stream = outputStream;
		_writer = new OutputStreamWriter( outputStream, initialCharset );
	}

	public void setCharset( final Charset charset )
		throws IOException
	{
		_writer.flush();
		_writer = new OutputStreamWriter( _stream, charset );
	}

	@Override
	public void write( final int c )
		throws IOException
	{
		_writer.write( c );
	}

	@Override
	public void write( final char[] cbuf )
		throws IOException
	{
		_writer.write( cbuf );
	}

	@Override
	public void write( final char[] cbuf, final int off, final int len )
		throws IOException
	{
		_writer.write( cbuf, off, len );
	}

	@Override
	public void write( final String str )
		throws IOException
	{
		_writer.write( str );
	}

	@Override
	public void write( final String str, final int off, final int len )
		throws IOException
	{
		_writer.write( str, off, len );
	}

	@Override
	public Writer append( final CharSequence csq )
		throws IOException
	{
		return _writer.append( csq );
	}

	@Override
	public Writer append( final CharSequence csq, final int start, final int end )
		throws IOException
	{
		return _writer.append( csq, start, end );
	}

	@Override
	public Writer append( final char c )
		throws IOException
	{
		return _writer.append( c );
	}

	@Override
	public void flush()
		throws IOException
	{
		_writer.flush();
	}

	@Override
	public void close()
		throws IOException
	{
		_writer.close();
	}
}
