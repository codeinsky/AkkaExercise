package akkaExercising;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ActorSystemTest {
	
	public static void main(String[] args) throws InterruptedException {
		ActorSystem system = ActorSystem.create("system" );
		final ActorRef parent = system.actorOf(ParentRouter.props() , "parent");
		parent.tell("pingStart", ActorRef.noSender());
		
//		for (int i = 0 ; i <20 ; i ++) {
//		parent.tell(new ParentRouter.Message("category1", "that is message for categro1 dispatcher"), ActorRef.noSender());
//		parent.tell(new ParentRouter.Message("category2", "that is message for categro1 dispatcher"), ActorRef.noSender());
//		}
		Thread.sleep(10000);
		System.out.println("Stopping from main");
		parent.tell("pingStop", ActorRef.noSender());
		Thread.sleep(10000);
		parent.tell("pingStart", ActorRef.noSender());
		Thread.sleep(10000);
		parent.tell("killTest", ActorRef.noSender());
		
//		final ActorRef child1 = system.actorOf(Category1.props() , "child1");
//		final ActorRef child2 = system.actorOf(Category2.props() , "child2");
//		final ActorRef boss = system.actorOf(Watcher.props(child1 , child2) , "BOSS");
//		boss.tell("on", ActorRef.noSender());
	
		
	
//		parent.tell("Test", ActorRef.noSender());
//		parent.tell(new ParentRouter.Message("category3", "that is message for categro1 dispatcher"), ActorRef.noSender());
//		parent.tell(new ParentRouter.Message("category3", "that is message for categro1 dispatcher"), ActorRef.noSender());
//		parent.tell(new ParentRouter.Message("category3", "that is message for categro1 dispatcher"), ActorRef.noSender());
//		
//		
		
		
		
		
//		ActorRef receiver = system.actorOf(ReceiverActor.Receiver.props(),"receiver");
//		ActorRef model = system.actorOf(Category1.props().withDispatcher("my-pinned-dispatcher"),"model1");
//		model.tell(new Category1.Messages("Telling the model1"), ActorRef.noSender());
	
//		receiver.tell(new ReceiverActor.Receiver.Echo(), ActorRef.noSender());
//		receiver.tell(new ReceiverActor.Receiver.Cars("Audi" , "That is the message for Audi logger"), ActorRef.noSender());
		

		
	}


}
