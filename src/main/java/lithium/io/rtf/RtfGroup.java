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

import java.util.*;

/**
 * RTF group.
 *
 * @author Gerrit Meinders
 */
public class RtfGroup
	extends AbstractRtfNode
{
	private final List<RtfNode> _nodes = new ArrayList<RtfNode>();

	/**
	 * Constructs a new instance.
	 */
	public RtfGroup()
	{
	}

	public List<RtfNode> getNodes()
	{
		return Collections.unmodifiableList( _nodes );
	}

	public void addNode( RtfNode node )
	{
		node.setParent( this );
		_nodes.add( node );
	}

	public void removeNode( RtfNode node )
	{
		node.setParent( null );
		_nodes.remove( node );
	}

	public void removeAllNodes()
	{
		_nodes.clear();
	}

	@Override
	public void accept( final RtfVisitor visitor )
	{
		final RtfVisitor.VisitResult visitResult = visitor.groupStart( this );
		if ( visitResult == RtfVisitor.VisitResult.CONTINUE )
		{
			for ( final RtfNode node : _nodes )
			{
				node.accept( visitor );
			}
		}
		visitor.groupEnd( this );
	}

	@Override
	public String toString()
	{
		return super.toString() + "[nodes=" + _nodes + "]";
	}
}
