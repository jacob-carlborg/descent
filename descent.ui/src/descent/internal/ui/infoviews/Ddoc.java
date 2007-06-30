package descent.internal.ui.infoviews;

import java.util.ArrayList;
import java.util.List;

public class Ddoc {
	
	private List<DdocSection> sections;
	private DdocSection paramsSection;
	private DdocSection macrosSection;
	
	public Ddoc() {
		this.sections = new ArrayList<DdocSection>();
	}
	
	public void addSection(DdocSection section) {
		this.sections.add(section);
		
		if (section.getKind() == DdocSection.PARAMS_SECTION) {
			paramsSection = section;
		} else if (section.getKind() == DdocSection.MACROS_SECTION) {
			macrosSection = section;
		}
	}
	
	public DdocSection[] getSections() {
		return sections.toArray(new DdocSection[sections.size()]);
	}
	
	public DdocSection getParamsSection() {
		return paramsSection;
	}
	
	public DdocSection getMacrosSection() {
		return macrosSection;
	}

}
