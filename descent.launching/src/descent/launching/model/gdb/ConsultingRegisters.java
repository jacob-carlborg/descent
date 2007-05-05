package descent.launching.model.gdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

public class ConsultingRegisters implements IState {
	
	public List<IRegister> fRegisters = new ArrayList<IRegister>();
	private final IRegisterGroup fRegisterGroup;
	private final GdbDebugger fCli;
	
	public ConsultingRegisters(GdbDebugger cli, IRegisterGroup registerGroup) {
		this.fCli = cli;
		this.fRegisterGroup = registerGroup;
	}
	
	public void interpret(String text) throws DebugException, IOException {
		if (text.equals("(gdb) ")) {
			fCli.notifyStateReturn();
		} else {
			parseRegisters(text, fRegisterGroup);
		}
	}
	
	public void interpretError(String text) throws DebugException, IOException {
		// Nothing to do
	}
	
	private void parseRegisters(String text, IRegisterGroup group) {
		String[] splits = text.split("\\p{Space}");
		
		if (splits.length < 3) return;
		
		String name = splits[0];
		for(int i = 1; i < splits.length; i++) {
			String value = splits[i].trim();
			if (value.length() != 0) {
				fRegisters.add(fCli.fFactory.newRegister(group, name, value));
				return;
			}
		}
		
	}
	
	@Override
	public String toString() {
		return "consulting registers";
	}
	
}