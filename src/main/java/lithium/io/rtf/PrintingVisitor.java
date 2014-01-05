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
 * Prints information about visited nodes.
 *
 * @author Gerrit Meinders
 */
public class PrintingVisitor
	implements RtfVisitor
{
	@Override
	public void visitText( final TextNode text )
	{
		System.out.println( "text: " + text.getText() );
	}

	@Override
	public void visitControlWord( final ControlWord controlWord )
	{
		Integer numericParameter = controlWord.getNumericParameter();
		if ( numericParameter == null )
		{
			System.out.println( "controlWord: " + controlWord.getWord() );
		}
		else
		{
			System.out.println( "controlWord: " + controlWord.getWord() + numericParameter );
		}
	}

	@Override
	public void visitControlSymbol( final ControlSymbol controlSymbol )
	{
		System.out.println( "controlSymbol: " + controlSymbol.getSymbol() );
	}

	@Override
	public VisitResult groupStart( final RtfGroup group )
	{
		System.out.println( "groupStart" );
		return VisitResult.CONTINUE;
	}

	@Override
	public void groupEnd( final RtfGroup group )
	{
		System.out.println( "groupEnd" );
	}
}
