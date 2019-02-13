package akkaExercising;
import akka.actor.AbstractActorWithStash;
import akka.actor.Props;
import akka.actor.UnrestrictedStash;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;


public class CarMessageLogger1 extends AbstractActorWithStash{
	  
	// CarMessageLogger1 has two states
	// 1. Stashing mode - stores 5 messages in the Stash and then processing  
	// 2. Processing mode - once stash has reach 5 messages count , actor starts processing 
	private final PartialFunction<Object, BoxedUnit> stashing;
	private final PartialFunction<Object, BoxedUnit> proccessing;
	
	private int count = 0 ;

	
	static Props props() {
		return Props.create(CarMessageLogger1.class);
	}
	
	// initialization block 
	{
	
		stashing = ReceiveBuilder
				.match(ParentRouterWatcher.Message.class ,  msg ->{ loggerStash(msg , this);})
				.matchEquals("ping", msg -> { 
					sender().tell("pong", self());
					})
				.build();
		
		proccessing = ReceiveBuilder
				.match(ParentRouterWatcher.Message.class ,  msg ->{ loggerProccess(msg , this);})
				.matchEquals("ping", msg -> { 
					sender().tell("pong", self());
					})
				.build();
		// actor starts with stashing mode 
		receive(stashing);
	}
	
	
    //Actor stores messes in the stash - 5 messages and the changes state 
	private void loggerStash(ParentRouterWatcher.Message message , UnrestrictedStash stash) {
			count++;
			stash.stash();
			System.out.println("Stashing messages Category 1, Message number " + count);
			
	if (count==5) {	
			System.out.println("Switched to proccessing");
			stash.unstashAll();
			getContext().become(proccessing);
		
		}
	
	}
	// Processing mode, procces 5 messages from the Stash and then switches back to 
	// to stashing mode (store)
	private void loggerProccess(ParentRouterWatcher.Message message , UnrestrictedStash stash ) {
		System.out.println("Message proccesed by Car Category 1 " +  message.messageBody);
			count--;
			if (count==0) {
				getContext().become(stashing);
				System.out.println("switching back to Sttashing mode");
			}
			System.out.println(count);

		
	}
	
}