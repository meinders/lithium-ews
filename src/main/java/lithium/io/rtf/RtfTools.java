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
 * Provides utility methods for working with RTF documents.
 *
 * @author Gerrit Meinders
 */
public class RtfTools
{
	public static RtfGroup getGroupByFirstWord( final RtfNode node, final String word )
	{
		final RtfGroup[] result = new RtfGroup[ 1 ];
		node.accept( new RtfVisitor()
		{
			@Override
			public void visitText( final TextNode text )
			{
			}

			@Override
			public void visitControlWord( final ControlWord controlWord )
			{
				if ( word.equals( controlWord.getWord() ) )
				{
					result[ 0 ] = controlWord.getParent();
				}
			}

			@Override
			public void visitControlSymbol( final ControlSymbol controlSymbol )
			{
			}

			@Override
			public VisitResult groupStart( final RtfGroup group )
			{
				return result[ 0 ] == null ? VisitResult.CONTINUE : VisitResult.SKIP_SUBTREE;
			}

			@Override
			public void groupEnd( final RtfGroup group )
			{
			}
		} );
		return result[ 0 ];
	}
}
