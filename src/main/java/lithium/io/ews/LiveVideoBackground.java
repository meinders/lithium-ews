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
 * Background showing a video stream.
 *
 * @author Gerrit Meinders
 */
public class LiveVideoBackground
	implements Background
{
	private String _name;

	private ScheduleEntry.AspectRatio _aspectRatio;

	/**
	 * Constructs a new instance.
	 */
	public LiveVideoBackground()
	{
	}

	public void setName( String name )
	{
		_name = name;
	}

	public String getName()
	{
		return _name;
	}

	public void setAspectRatio( ScheduleEntry.AspectRatio aspectRatio )
	{
		_aspectRatio = aspectRatio;
	}

	public ScheduleEntry.AspectRatio getAspectRatio()
	{
		return _aspectRatio;
	}
}
