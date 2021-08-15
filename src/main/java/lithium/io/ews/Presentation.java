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

import java.util.*;

/**
 * Provides a preview of a slide show presentation. The presentation itself is
 * stored in its native format as {@link BinaryContent}.
 *
 * @author Gerrit Meinders
 */
public class Presentation
{
	private final List<Slide> _slides = new ArrayList<Slide>();

	/**
	 * Unknown header content.
	 */
	private byte[] _unknown;

	private int magicValue = 0;

	/**
	 * Constructs a new instance.
	 */
	public Presentation()
	{
	}

	public void addSlide( Slide slide )
	{
		_slides.add( slide );
	}

	public List<Slide> getSlides()
	{
		return Collections.unmodifiableList( _slides );
	}

	public void setUnknown( byte[] unknown )
	{
		_unknown = unknown;
	}

	public byte[] getUnknown()
	{
		return _unknown;
	}

	public int getMagicValue()
	{
		return magicValue;
	}

	public void setMagicValue( final int magicValue )
	{
		this.magicValue = magicValue;
	}
}
