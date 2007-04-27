package descent.launching.model.ddbg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

public class ConsultingRegisters implements IState {
	
	public List<IRegister> fRegisters = new ArrayList<IRegister>();
	private final IRegisterGroup fRegisterGroup;
	private final DdbgCli fCli;
	
	public ConsultingRegisters(DdbgCli cli, IRegisterGroup registerGroup) {
		this.fCli = cli;
		this.fRegisterGroup = registerGroup;
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("->")) {
			fCli.notifyStateReturn();
		} else {
			parseRegisters(text, fRegisterGroup);
		}
	}
	
	private void parseRegisters(String text, IRegisterGroup group) {
		for (int i = 0; i < 4; i++) {
			String sub;
			if (i == 3) {
				sub = text.substring(13*i);
			} else {
				sub = text.substring(13*i, 13*(i + 1));
			}
			int indexOfEqual = sub.indexOf('=');
			String name = sub.substring(0, indexOfEqual).trim();
			String value = sub.substring(indexOfEqual + 1).trim();
			fRegisters.add(fCli.fFactory.newRegister(group, name, value));
		}
	}
	
	@Override
	public String toString() {
		return "consulting registers";
	}
	
}