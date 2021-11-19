package net.paulhertz.aifile;

/**
 * Finds a DisplayComponent with specified ID. If the component is found, {@link compIsFound()}
 * returns {@code true} and the component can be retrieved from {@link getFoundComp()}.
 * Typically, you would cache the ID or IDs upon instantiating the component, for later retrieval.
 *
 */
public class FindComponentWithIDVisitor extends ComponentVisitor {
	private int idToFind;
	private DisplayComponent foundComp;
	private boolean compIsFound;
	
	private FindComponentWithIDVisitor() {
		
	}	
	
	public static FindComponentWithIDVisitor makeFindComponentWithIDVisitor(int idToFind) {
		FindComponentWithIDVisitor v = new FindComponentWithIDVisitor();
		v.setIdToFind(idToFind);
		v.setFoundComp(null);
		return v;
	}
	
	/**
	 * @return the idToFind
	 */
	public int getIdToFind() {
		return idToFind;
	}

	/**
	 * @param idToFind the idToFind to set
	 */
	public void setIdToFind(int idToFind) {
		this.idToFind = idToFind;
	}

	/**
	 * @return the foundComp
	 */
	public DisplayComponent getFoundComp() {
		return foundComp;
	}

	/**
	 * @param foundComp the foundComp to set
	 */
	public void setFoundComp(DisplayComponent foundComp) {
		this.foundComp = foundComp;
	}

	
	/**
	 * @return the value of compIsFound, {@code true} if a DisplayComponent with the supplied ID was found, {@code false} otherwise.
	 */
	public boolean compIsFound() {
		return compIsFound;
	}

	/**
	 * @param compIsFound the compIsFound to set
	 */
	public void setCompIsFound(boolean compIsFound) {
		this.compIsFound = compIsFound;
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.ComponentVisitor#visitDocumentComponent(net.paulhertz.aifile.DocumentComponent)
	 */
	@Override
	public void visitDocumentComponent(DocumentComponent comp) {
		if (this.compIsFound()) return;
		if (comp.id() == this.idToFind) {
			this.foundComp = comp;
			this.setCompIsFound(true);
		}
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.ComponentVisitor#visitLayerComponent(net.paulhertz.aifile.LayerComponent)
	 */
	@Override
	public void visitLayerComponent(LayerComponent comp) {
		if (this.compIsFound()) return;
		if (comp.id() == this.idToFind) {
			this.foundComp = comp;
			this.setCompIsFound(true);
		}
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.ComponentVisitor#visitGroupComponent(net.paulhertz.aifile.GroupComponent)
	 */
	@Override
	public void visitGroupComponent(GroupComponent comp) {
		if (this.compIsFound()) return;
		if (comp.id() == this.idToFind) {
			this.foundComp = comp;
			this.setCompIsFound(true);
		}
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.ComponentVisitor#visitCustomComponent(net.paulhertz.aifile.CustomComponent)
	 */
	@Override
	public void visitCustomComponent(CustomComponent comp) {
		if (this.compIsFound()) return;
		if (comp.id() == this.idToFind) {
			this.foundComp = comp;
			this.setCompIsFound(true);
		}
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.ComponentVisitor#visitBezShape(net.paulhertz.aifile.BezShape)
	 */
	@Override
	public void visitBezShape(BezShape comp) {
		if (this.compIsFound()) return;
		if (comp.id() == this.idToFind) {
			this.foundComp = comp;
			this.setCompIsFound(true);
		}
	}

	/* (non-Javadoc)
	 * @see net.paulhertz.aifile.ComponentVisitor#visitPointText(net.paulhertz.aifile.PointText)
	 */
	@Override
	public void visitPointText(PointText comp) {
		if (this.compIsFound()) return;
		if (comp.id() == this.idToFind) {
			this.foundComp = comp;
			this.setCompIsFound(true);
		}
	}

}
