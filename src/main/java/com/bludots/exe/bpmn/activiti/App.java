package com.bludots.exe.bpmn.activiti;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.apache.commons.io.FileUtils;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args)
			throws FileNotFoundException, IOException {// create process
		// engine
		ProcessEngineConfiguration cfg = ProcessEngineConfiguration
				.createStandaloneInMemProcessEngineConfiguration();
		ProcessEngine processEngine = cfg.buildProcessEngine();

		// create a new process
		Process p = new Process();
		p.setId("test");
		p.setName("test");

		// create start event
		StartEvent startEvent = new StartEvent();
		startEvent.setName("start");
		startEvent.setId("start");

		// create end event
		EndEvent endEvent = new EndEvent();
		endEvent.setName("end");
		endEvent.setId("end");

		// create a new task
		UserTask task = new UserTask();
		task.setName("Task1");
		task.setId("task1");

		ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
		exclusiveGateway.setName("Decision");
		exclusiveGateway.setId("xg1");

		// create a flow from start to exclusivegateway
		SequenceFlow flow0 = new SequenceFlow();
		flow0.setId("flow0");
		flow0.setSourceRef("start");
		flow0.setTargetRef("xg1");

		// create a flow from exclusivegateway to task
		SequenceFlow flow1 = new SequenceFlow();
		flow1.setId("flow1");
		flow1.setSourceRef("xg1");
		flow1.setTargetRef("task1");

		// create a flow from task to end
		SequenceFlow flow2 = new SequenceFlow();
		flow2.setId("flow2");
		flow2.setSourceRef("task1");
		flow2.setTargetRef("end");

		// add the elements to the process
		p.addFlowElement(flow0);
		p.addFlowElement(flow1);
		p.addFlowElement(flow2);
		p.addFlowElement(task);
		p.addFlowElement(exclusiveGateway);
		p.addFlowElement(endEvent);
		p.addFlowElement(startEvent);

		// add outgoing flows
		List<SequenceFlow> startEventoutgoingFlows = new ArrayList<SequenceFlow>();
		startEventoutgoingFlows.add(flow0);
		startEvent.setOutgoingFlows(startEventoutgoingFlows);

		List<SequenceFlow> gatewayOutgoingFlows = new ArrayList<SequenceFlow>();
		gatewayOutgoingFlows.add(flow1);
		exclusiveGateway.setOutgoingFlows(gatewayOutgoingFlows);

		List<SequenceFlow> task1EventoutgoingFlows = new ArrayList<SequenceFlow>();
		task1EventoutgoingFlows.add(flow2);
		task.setOutgoingFlows(task1EventoutgoingFlows);

		// create a bpmn model
		BpmnModel model = new BpmnModel();
		// add the process to the model
		model.addProcess(p);

		// Add auto layout
		new BpmnAutoLayout(model).execute();

		byte[] convertToXML = new BpmnXMLConverter().convertToXML(model);
		try (FileOutputStream fileOuputStream = new FileOutputStream(
				"c://temp/test.bpmn20.xml")) {
			fileOuputStream.write(convertToXML);
			fileOuputStream.close();
		}

		// write the file
		FileUtils.copyInputStreamToFile(
				cfg.getProcessDiagramGenerator().generatePngDiagram(model),
				new File("c://temp/test.png"));
	}
}
