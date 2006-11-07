package descent.internal.core.dom;

public class Loc {
	
	public String filename;
	public int linnum;
	
	public Loc() {
		this.filename = null;
		this.linnum = 0;
	}
	
	public Loc(int x) {
		this.filename = null;
		this.linnum = x;
	}
	
	public Loc(Loc loc) {
		this.filename = loc.filename;
		this.linnum = loc.linnum;
	}
	
	@Override
	public Loc clone() throws CloneNotSupportedException {
		return (Loc) super.clone();
	}
	
	@Override
	public String toString() {
		return String.valueOf(linnum);
	}

}
