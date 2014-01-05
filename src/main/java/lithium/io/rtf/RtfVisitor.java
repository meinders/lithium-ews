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
 * Visitor for RTF documents.
 *
 * @author Gerrit Meinders
 */
public interface RtfVisitor
{
	enum VisitResult {
		/**
		 * Continue visiting nodes, including nested nodes.
		 */
		CONTINUE,

		/**
		 * Skip any nodes nested under the current node.
		 */
		SKIP_SUBTREE,
	}

	void visitText( TextNode text );

	void visitControlWord( ControlWord controlWord );

	void visitControlSymbol( ControlSymbol controlSymbol );

	VisitResult groupStart( RtfGroup group );

	void groupEnd( RtfGroup group );
}
