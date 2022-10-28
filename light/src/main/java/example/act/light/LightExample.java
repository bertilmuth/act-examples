package example.act.light;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;

public class LightExample {
	interface Trigger{ }
	class TurnOn implements Trigger{ }
	class TurnOff implements Trigger{ }
	
	enum LightState{ON, OFF};

	public static void main(String[] args) {
		new LightExample().run();
	}

	private void run() {
		State<LightState, Trigger> on = state("On", s -> s.equals(LightState.ON));
		State<LightState, Trigger> off = state("Off", s -> s.equals(LightState.OFF));
		
		Statemachine<LightState, Trigger> statemachine = Statemachine.builder()
			.states(on, off)
			.transitions(
				transition(off, on, when(TurnOn.class, consumeWith(this::turnOn))),
				transition(on, off, when(TurnOff.class, consumeWith(this::turnOff)))
			)
			.build();
		
		Data<LightState, Trigger> turnOnEvent = eventIn(LightState.OFF, new TurnOn());
		LightState lightOnState = 
			statemachine.actOn(turnOnEvent).state();
		
		statemachine.actOn(eventIn(lightOnState, new TurnOn()));
		statemachine.actOn(eventIn(lightOnState, new TurnOff()));
	}

	private Data<LightState, Trigger> eventIn(LightState lightState, Trigger event) {
		return data(lightState, event);
	}

	private LightState turnOn(LightState state, TurnOn t) {
		System.out.println("Turning Light On");
		return LightState.ON;
	}
	
	private LightState turnOff(LightState state, TurnOff t) {
		System.out.println("Turning Light Off");
		return LightState.OFF;
	}
}
