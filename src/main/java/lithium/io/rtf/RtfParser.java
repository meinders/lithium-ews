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
 * Parses RTF documents.
 *
 * @author Gerrit Meinders
 */
public class RtfParser
{
	private Reader _reader;

	private int _next;

	private StringBuilder _builder = new StringBuilder();

	private Charset _charset = Charset.forName( Config.charset );

	/**
	 * Constructs a new instance.
	 */
	public RtfParser()
	{
	}

	public RtfGroup parse( final InputStream in )
		throws IOException
	{
		final BufferedInputStream bin = new BufferedInputStream( in );
		_charset = detectCharset( bin );
		final InputStreamReader reader = new InputStreamReader( bin, _charset );
		_reader = reader;
		_next = reader.read();

		return parseGroup();
	}

	private RtfGroup parseGroup()
		throws IOException
	{
		accept( '{' );
		final RtfGroup result = new RtfGroup();

		while ( _next != -1 )
		{
			if ( _next == '}' )
			{
				break;
			}
			else if ( _next == '\\' )
			{
				result.addNode( parseControlToken() );
			}
			else if ( _next == '{' )
			{
				result.addNode( parseGroup() );
			}
			else
			{
				result.addNode( parseText() );
			}
		}

		accept( '}' );
		return result;
	}

	private TextNode parseText()
		throws IOException
	{
		if ( _next == '\\' || _next == '}' || _next == '{' )
		{
			throw new ParseException( "Expected text content, but was '" + (char)_next + "'." );
		}

		do
		{
			_builder.append( (char)_next );
			accept();
		}
		while ( _next != '\\' && _next != '}' && _next != '{' );

		final TextNode result = new TextNode();
		result.setText( _builder.toString() );
		_builder.setLength( 0 );
		return result;
	}

	private RtfNode parseControlToken()
		throws IOException
	{
		accept( '\\' );

		if ( _next >= 'a' && _next <= 'z' )
		{
			return parseControlWord();
		}
		else if ( _next == '\'' )
		{
			// Get hex code for special char
			accept();
			int asciiCode = Character.digit( _next, 16 );
			accept();
			asciiCode = ( asciiCode << 4 ) |
			            Character.digit( _next, 16 );
			accept();

			// Convert hex code to char with the correct charset encoding
			final byte[] asciiBytes = { (byte)asciiCode };
			final String specialChar = new String( asciiBytes, _charset );

			return new TextNode( specialChar );
		}
		else
		{
			final char symbol = (char)_next;
			accept();

			if (symbol == '\\' || symbol == '{' || symbol == '}') {
				return new TextNode(Character.toString(symbol));
			}

			final ControlSymbol result = new ControlSymbol();
			result.setSymbol( symbol );
			return result;
		}
	}

	private ControlWord parseControlWord()
		throws IOException
	{
		if ( _next < 'a' || _next > 'z' )
		{
			throw new ParseException( "Expected [a-z], but was '" + (char)_next + "'." );
		}

		final ControlWord result = new ControlWord();

		do
		{
			_builder.append( (char)_next );
			accept();
		}
		while ( _next >= 'a' && _next <= 'z' );
		result.setWord( _builder.toString() );
		_builder.setLength( 0 );

		if ( _next >= '0' && _next <= '9' || _next == '-' )
		{
			do
			{
				_builder.append( (char)_next );
				accept();
			}
			while ( _next >= '0' && _next <= '9' );
			final String number = _builder.toString();
			result.setNumericParameter( Integer.valueOf( number ) );
			_builder.setLength( 0 );
		}

		if ( _next == ' ' )
		{
			accept();
			result.setDelimitedBySpace( true );
		}

		return result;
	}

	private void accept( int expected )
		throws IOException
	{
		if ( _next == -1 )
		{
			throw new ParseException( "Expected '" + (char)expected + "', but was end-of-file." );
		}

		if ( _next != expected )
		{
			throw new ParseException( "Expected '" + (char)expected + "', but was '" + (char)_next + "'." );
		}

		_next = _reader.read();
	}

	private void accept()
		throws IOException
	{
		if ( _next == -1 )
		{
			throw new ParseException( "Unexpected end-of-file." );
		}

		_next = _reader.read();
	}

	private Charset detectCharset( final BufferedInputStream bin )
		throws IOException
	{
		bin.mark( 1024 );

		final InputStreamReader headerReader = new InputStreamReader(bin, Config.charset);
		accept( headerReader, '{' );
		accept( headerReader, '\\' );
		accept( headerReader, 'r' );
		accept( headerReader, 't' );
		accept( headerReader, 'f' );
		headerReader.read(); // version number
		accept( headerReader, '\\' );

		final StringBuilder charsetBuffer = new StringBuilder();
		int read;
		while ( ( ( read = headerReader.read() ) != -1 ) && ( read >= 'a' ) && ( read <= 'z' ) )
		{
			charsetBuffer.append( (char)read );
		}
		final String charsetName = charsetBuffer.toString();
		final Charset charset;
		if ( "ansi".equals( charsetName ) )
		{
			charset = Charset.forName( Config.charset );
		}
		else
		{
			charset = Charset.forName( charsetName );
		}

		bin.reset();
		return charset;
	}

	private void accept( final Reader in, final int expected )
		throws IOException
	{
		final int read = in.read();
		if ( read != expected )
		{
			throw new ParseException( "Expected '" + (char)expected + "', but was '" + (char)read + "'." );
		}
	}
}
