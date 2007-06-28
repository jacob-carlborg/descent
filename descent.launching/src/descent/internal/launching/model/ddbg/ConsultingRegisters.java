package descent.internal.launching.model.ddbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

public class ConsultingRegisters implements IState {
	
	public List<IRegister> fRegisters = new ArrayList<IRegister>();
	private final IRegisterGroup fRegisterGroup;
	private final DdbgDebugger fCli;
	
	private String lastName;
	private String lastValue;
	
	public ConsultingRegisters(DdbgDebugger cli, IRegisterGroup registerGroup) {
		this.fCli = cli;
		this.fRegisterGroup = registerGroup;
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("->")) {
			fRegisters.add(fCli.fFactory.newRegister(fRegisterGroup, lastName, lastValue));
			fCli.notifyStateReturn();
		} else {
			parseRegisters(text);
		}
	}
	
	private void parseRegisters(String text) {
		String[] pieces = text.split("\t");
		for(String piece : pieces) {
			int indexOfEqual = piece.indexOf('=');
			if (indexOfEqual == -1) {
				continue;
			}
			String name = piece.substring(0, indexOfEqual).trim();
			String value = piece.substring(indexOfEqual + 1).trim();
			if (name.length() == 0) {
				lastValue += "\n" + value;
			} else {
				if (lastName != null) {
					fRegisters.add(fCli.fFactory.newRegister(fRegisterGroup, lastName, lastValue));
				}
				
				lastName = name;
				lastValue = value;				
			}
		}
	}
	
	@Override
	public String toString() {
		return "consulting registers";
	}
	
}