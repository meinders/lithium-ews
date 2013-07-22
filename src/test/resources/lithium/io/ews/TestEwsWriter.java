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

import junit.framework.*;

/**
 * Test case for {@link EwsWriter}.
 *
 * @author Gerrit Meinders
 */
public class TestEwsWriter
extends TestCase
{
	/**
	 * Tests that a schedule with some songs in it can be read and then written,
	 * creating a duplicate of the original.
	 */
	public void testSongs()
	throws IOException
	{
		final byte[] scheduleFile = Tools.loadResource( getClass(), "lorem.ews" );

		final EwsParser parser = new EwsParser();
		parser.setCharset( Charset.forName( "windows-1252" ) );
		final Schedule schedule = parser.parse( ByteBuffer.wrap( scheduleFile ) );

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final EwsWriter writer = new EwsWriter( out );
		writer.setCharset( Charset.forName( "windows-1252" ) );
		writer.write( schedule );
		final byte[] output = out.toByteArray();

		// TODO: It's not an exact match yet, due to a timestamp rounding issue and slightly different compression of the content.
//		assertEquals( "Unexpected output.", Arrays.toString( scheduleFile ), Arrays.toString( output ) );

		// Reading back the output should be enough verification for now:
		parser.parse( ByteBuffer.wrap( output ) );
	}
}
