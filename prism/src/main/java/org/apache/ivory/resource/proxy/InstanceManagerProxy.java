package org.apache.ivory.resource.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ivory.IvoryException;
import org.apache.ivory.IvoryRuntimException;
import org.apache.ivory.IvoryWebException;
import org.apache.ivory.monitors.Dimension;
import org.apache.ivory.monitors.Monitored;
import org.apache.ivory.resource.APIResult;
import org.apache.ivory.resource.AbstractInstanceManager;
import org.apache.ivory.resource.InstancesResult;
import org.apache.ivory.resource.InstancesResult.Instance;
import org.apache.ivory.resource.channel.Channel;
import org.apache.ivory.resource.channel.ChannelFactory;

@Path("instance")
public class InstanceManagerProxy extends AbstractInstanceManager {

    private final Map<String, Channel> processInstanceManagerChannels = new HashMap<String, Channel>();

    public InstanceManagerProxy() {
        try {
            Set<String> colos = getAllColos();

            for (String colo : colos) {
                initializeFor(colo);
            }
        } catch (IvoryException e) {
            throw new IvoryRuntimException("Unable to initialize channels", e);
        }
    }

    private void initializeFor(String colo) throws IvoryException {
        processInstanceManagerChannels.put(colo, ChannelFactory.get("ProcessInstanceManager", colo));
    }

    private Channel getInstanceManager(String colo) throws IvoryException {
        if (!processInstanceManagerChannels.containsKey(colo)) {
            initializeFor(colo);
        }
        return processInstanceManagerChannels.get(colo);
    }

    @GET
    @Path("running/{type}/{entity}")
    @Produces(MediaType.APPLICATION_JSON)
    @Monitored(event="running")
    @Override
    public InstancesResult getRunningInstances(@Dimension("entityType") @PathParam("type") final String type,
                                                      @Dimension("entityName") @PathParam("entity") final String entity,
                                                      @Dimension("colo") @QueryParam("colo") String colo) {
        return new InstanceProxy() {
            @Override
            protected InstancesResult doExecute(String colo) throws IvoryException {
                return getInstanceManager(colo).
                        invoke("getRunningInstances", type, entity, colo);
            }
        }.execute(colo, type, entity);
    }

    @GET
    @Path("status/{type}/{entity}")
    @Produces(MediaType.APPLICATION_JSON)
    @Monitored(event="instance-status")
    @Override
    public InstancesResult getStatus(@Dimension("entityType") @PathParam("type") final String type,
                                            @Dimension("entityName") @PathParam("entity") final String entity,
                                            @Dimension("start-time") @QueryParam("start") final String startStr,
                                            @Dimension("end-time") @QueryParam("end") final String endStr,
                                            @Dimension("colo") @QueryParam("colo") final String colo) {
        return new InstanceProxy() {
            @Override
            protected InstancesResult doExecute(String colo) throws IvoryException {
                return getInstanceManager(colo).invoke("getStatus",
                        type, entity, startStr, endStr, colo);
            }
        }.execute(colo, type, entity);
    }
    
	@GET
	@Path("logs/{type}/{entity}")
	@Produces(MediaType.APPLICATION_JSON)
	@Monitored(event = "instance-logs")
	@Override
	public InstancesResult getLogs(
			@Dimension("type") @PathParam("type") final String type,
			@Dimension("entity") @PathParam("entity") final String entity,
			@Dimension("start-time") @QueryParam("start") final String startStr,
			@Dimension("end-time") @QueryParam("end") final String endStr,
			@Dimension("colo") @QueryParam("colo") final String colo,
			@Dimension("run-id") @QueryParam("runid") final String runId) {
        return new InstanceProxy() {
            @Override
            protected InstancesResult doExecute(String colo) throws IvoryException {
                return getInstanceManager(colo).invoke("getLogs",
                        type, entity, startStr, endStr, colo, runId);
            }
        }.execute(colo, type, entity);
    }

    @POST
    @Path("kill/{type}/{entity}")
    @Produces(MediaType.APPLICATION_JSON)
    @Monitored(event="kill-instance")
    @Override
    public InstancesResult killInstance(@Context HttpServletRequest request,
                                        @Dimension("entityType") @PathParam("type") final String type,
                                        @Dimension("entityName") @PathParam("entity") final String entity,
                                        @Dimension("start-time") @QueryParam("start") final String startStr,
                                        @Dimension("end-time") @QueryParam("end") final String endStr,
                                        @Dimension("colo") @QueryParam("colo") final String colo) {

        final HttpServletRequest bufferedRequest = new BufferedRequest(request);
        return new InstanceProxy() {
            @Override
            protected InstancesResult doExecute(String colo) throws IvoryException {
                return getInstanceManager(colo).invoke("killInstance",
                    bufferedRequest, type, entity, startStr, endStr, colo);
            }
        }.execute(colo, type, entity);
    }

    @POST
    @Path("suspend/{type}/{entity}")
    @Produces(MediaType.APPLICATION_JSON)
	@Monitored(event="suspend-instance")
    @Override
    public InstancesResult suspendInstance(@Context HttpServletRequest request,
                                           @Dimension("entityType") @PathParam("type") final String type,
                                           @Dimension("entityName") @PathParam("entity") final String entity,
                                           @Dimension("start-time") @QueryParam("start") final String startStr,
                                           @Dimension("end-time") @QueryParam("end") final String endStr,
                                           @Dimension("colo") @QueryParam("colo") String colo) {
        final HttpServletRequest bufferedRequest = new BufferedRequest(request);
        return new InstanceProxy() {
            @Override
            protected InstancesResult doExecute(String colo) throws IvoryException {
                return getInstanceManager(colo).invoke("suspendInstance",
                    bufferedRequest, type, entity, startStr, endStr, colo);
            }
        }.execute(colo, type, entity);
    }

    @POST
    @Path("resume/{type}/{entity}")
    @Produces(MediaType.APPLICATION_JSON)
	@Monitored(event="resume-instance")
    @Override
    public InstancesResult resumeInstance(@Context HttpServletRequest request,
                                          @Dimension("entityType") @PathParam("type") final String type,
                                          @Dimension("entityName") @PathParam("entity") final String entity,
                                          @Dimension("start-time") @QueryParam("start") final String startStr,
                                          @Dimension("end-time") @QueryParam("end") final String endStr,
                                          @Dimension("colo") @QueryParam("colo") String colo) {

        final HttpServletRequest bufferedRequest = new BufferedRequest(request);
        return new InstanceProxy() {
            @Override
            protected InstancesResult doExecute(String colo) throws IvoryException {
                return getInstanceManager(colo).invoke("resumeInstance",
                    bufferedRequest, type, entity, startStr, endStr, colo);
            }
        }.execute(colo, type, entity);
    }

    @POST
    @Path("rerun/{type}/{entity}")
    @Produces(MediaType.APPLICATION_JSON)
	@Monitored(event="re-run-instance")
    @Override
    public InstancesResult reRunInstance(@Dimension("entityType") @PathParam("type") final String type,
                                                @Dimension("entityName") @PathParam("entity") final String entity,
                                                @Dimension("start-time") @QueryParam("start") final String startStr,
                                                @Dimension("end-time") @QueryParam("end") final String endStr,
                                                @Context HttpServletRequest request,
                                                @Dimension("colo") @QueryParam("colo") String colo) {

        final HttpServletRequest bufferedRequest = new BufferedRequest(request);
        return new InstanceProxy() {
            @Override
            protected InstancesResult doExecute(String colo) throws IvoryException {
                return getInstanceManager(colo).invoke("reRunInstance",
                    type, entity, startStr, endStr, bufferedRequest, colo);
            }
        }.execute(colo, type, entity);
    }

    private abstract class InstanceProxy {

        public InstancesResult execute(String coloExpr, String type, String name) {
            Set<String> colos = getColosFromExpression(coloExpr, type, name);

            Map<String, InstancesResult> results = new HashMap<String, InstancesResult>();
            for (String colo:colos) {
                try {
                    APIResult resultHolder = doExecute(colo);
                    if (resultHolder instanceof InstancesResult) {
                        results.put(colo, (InstancesResult) resultHolder);
                    } else {
                        throw new IvoryException(resultHolder.getMessage());
                    }
                } catch (IvoryException e) {
                    results.put(colo, new InstancesResult(APIResult.Status.FAILED,
                            e.getClass().getName() + "::" + e.getMessage(),
                            new InstancesResult.Instance[0]));
                }
            }
            InstancesResult finalResult = consolidateInstanceResult(results);
            if (finalResult.getStatus() != APIResult.Status.SUCCEEDED) {
                throw IvoryWebException.newException(finalResult, Response.Status.BAD_REQUEST);
            } else {
                return finalResult;
            }
        }

        protected abstract InstancesResult doExecute(String colo) throws IvoryException;
    }
    
    private InstancesResult consolidateInstanceResult(Map<String, InstancesResult> results) {
        if (results == null || results.isEmpty())
            return null;

        StringBuilder message = new StringBuilder();
        StringBuilder requestIds = new StringBuilder();
        List<Instance> instances = new ArrayList<Instance>();
        int statusCount = 0;
        for (String colo:results.keySet()) {
            InstancesResult result = results.get(colo);
            message.append(colo).append('/').append(result.getMessage()).append('\n');
            requestIds.append(colo).append('/').append(result.getRequestId()).append('\n');
            statusCount += result.getStatus().ordinal();

            if (result.getInstances() == null) continue;

            for (Instance instance : result.getInstances()) {
            	instance.instance = instance.getInstance();
            	instances.add(instance);
            }
        }
        Instance[] arrInstances = new Instance[instances.size()];
        APIResult.Status status = (statusCount == 0) ? APIResult.Status.SUCCEEDED
                : ((statusCount == results.size() * 2) ? APIResult.Status.FAILED : APIResult.Status.PARTIAL);
        InstancesResult result = new InstancesResult(status, message.toString(), instances.toArray(arrInstances));
        result.setRequestId(requestIds.toString());
        return result;
    }
}
