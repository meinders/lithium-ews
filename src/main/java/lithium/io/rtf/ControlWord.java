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

/**
 * RTF control word.
 *
 * @author Gerrit Meinders
 */
public class ControlWord
	extends AbstractRtfNode
{
	private String _word;

	private Integer _numericParameter;

	private boolean _delimitedBySpace;

	/**
	 * Constructs a new instance.
	 */
	public ControlWord()
	{
	}

	public void setWord( final String word )
	{
		_word = word;
	}

	public String getWord()
	{
		return _word;
	}

	public void setNumericParameter( final Integer numericParameter )
	{
		_numericParameter = numericParameter;
	}

	public Integer getNumericParameter()
	{
		return _numericParameter;
	}

	public boolean isDelimitedBySpace()
	{
		return _delimitedBySpace;
	}

	public void setDelimitedBySpace( final boolean delimitedBySpace )
	{
		_delimitedBySpace = delimitedBySpace;
	}

	@Override
	public void accept( final RtfVisitor visitor )
	{
		visitor.visitControlWord( this );
	}

	@Override
	public String toString()
	{
		return super.toString() + "[word=" + _word + ",numeric=" + _numericParameter + ",space=" + _delimitedBySpace + "]";
	}
}
