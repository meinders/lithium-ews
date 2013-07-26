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

import java.awt.*;

/**
 * Two-color gradient.
 *
 * @author Gerrit Meinders
 */
public class GradientBackground
	implements Background
{
	private Color _color1;

	private Color _color2;

	private ScheduleEntry.GradientStyle _style;

	private ScheduleEntry.GradientVariant _variant;

	/**
	 * Constructs a new instance.
	 */
	public GradientBackground()
	{
	}

	public void setColor1( Color color1 )
	{
		_color1 = color1;
	}

	public Color getColor1()
	{
		return _color1;
	}

	public void setColor2( Color color2 )
	{
		_color2 = color2;
	}

	public Color getColor2()
	{
		return _color2;
	}

	public void setStyle( ScheduleEntry.GradientStyle style )
	{
		_style = style;
	}

	public ScheduleEntry.GradientStyle getStyle()
	{
		return _style;
	}

	public void setVariant( ScheduleEntry.GradientVariant variant )
	{
		_variant = variant;
	}

	public ScheduleEntry.GradientVariant getVariant()
	{
		return _variant;
	}
}
