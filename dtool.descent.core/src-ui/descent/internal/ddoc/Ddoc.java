package descent.internal.ddoc;

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
	
	public boolean isDitto() {
		if (sections.size() == 1) {
			DdocSection section = sections.get(0);
			return section.getKind() == DdocSection.NORMAL_SECTION
				&& section.getText().trim().equalsIgnoreCase("ditto"); //$NON-NLS-1$
		}
		return false; 
	}

	public void merge(Ddoc other) {
		for(DdocSection otherSection : other.getSections()) {
			switch(otherSection.getKind()) {
			case DdocSection.NORMAL_SECTION:
			case DdocSection.CODE_SECTION:
				addSection(otherSection);
				break;
			case DdocSection.PARAMS_SECTION:
				if (paramsSection == null) {
					paramsSection = otherSection;
				} else {
					paramsSection.addParameters(otherSection.getParameters());
				}
				break;
			case DdocSection.MACROS_SECTION:
				if (macrosSection == null) {
					macrosSection = otherSection;
				} else {
					macrosSection.addParameters(otherSection.getParameters());
				}
				break;
			}
		}
	}

	public void mergeMacros(Ddoc otherDdoc) {
		DdocSection otherMacros = otherDdoc.getMacrosSection();
		if (otherMacros == null) return;
		
		if (macrosSection == null) {
			macrosSection = otherMacros;
		} else {
			macrosSection.addParameters(otherMacros.getParameters());
		}
	}

}
