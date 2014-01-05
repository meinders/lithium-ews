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

/**
 * Writes an RTF document to a character stream or buffer.
 *
 * @author Gerrit Meinders
 */
class StringRtfWriter
extends RtfWriter
{
	private Appendable _out;

	public StringRtfWriter( final Appendable out )
	{
		_out = out;
	}

	@Override
	public void write( final char c )
		throws IOException
	{
		_out.append( c );
	}

	@Override
	public void write( final String s )
		throws IOException
	{
		_out.append( s );
	}
}
