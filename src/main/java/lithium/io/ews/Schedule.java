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
 * A schedule, the main object parsed from the EWS file format.
 *
 * @author Gerrit Meinders
 */
public class Schedule
{
	private final List<ScheduleEntry> _entries = new ArrayList<ScheduleEntry>();

	/**
	 * Constructs a new instance.
	 */
	public Schedule()
	{
	}

	public List<ScheduleEntry> getEntries()
	{
		return _entries;
	}
}
