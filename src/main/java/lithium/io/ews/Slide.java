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

/**
 * Provides a preview of a single slide from a presentation.
 *
 * @author Gerrit Meinders
 */
public class Slide
{
	private byte[] _content;

	private byte[] _unknown;

	/**
	 * Constructs a new instance.
	 */
	public Slide()
	{
	}

	public byte[] getContent()
	{
		return _content;
	}

	public void setContent( final byte[] content )
	{
		_content = content;
	}

	public void setUnknown( byte[] unknown )
	{
		_unknown = unknown;
	}

	public byte[] getUnknown()
	{
		return _unknown;
	}
}
