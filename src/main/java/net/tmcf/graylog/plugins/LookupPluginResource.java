package net.tmcf.graylog.plugins;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.graylog2.database.NotFoundException;
import org.graylog2.plugin.database.ValidationException;
import org.graylog2.plugin.rest.PluginRestResource;
import org.graylog2.shared.rest.resources.RestResource;
import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiOperation;

@RequiresAuthentication
@Api(value = "Lookup", description = "Replace field values based on mongo lookup data map")
@Path("/filters/lookup")
public class LookupPluginResource extends RestResource implements PluginRestResource {
	
	private final LookupService lookupService;

	@Inject
	public LookupPluginResource(LookupService lookupService) {
		this.lookupService = lookupService;
	}
	
	@GET
	@Timed
	@ApiOperation("Get all mappings")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<LookupMapDescription> getAll() {
		
		try {
			return lookupService.loadAll();
		} catch (org.graylog2.database.NotFoundException e) {
			return Collections.emptySet();
		}
	}
	
	@POST
    @Timed
    @ApiOperation(value = "Create a mapping for lookup filter", notes = "It can take up to a second until the change is applied")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createNewMapping(@ApiParam(name = "mapping", required = true) 
		@Valid @NotNull LookupMapDescription lookupMapDescription) throws ValidationException {

		final LookupMapDescription savedMapping = lookupService.save(lookupMapDescription);
		final URI mappingUri = getUriBuilderToSelf().path(LookupPluginResource.class).path("{mappingId}").build(savedMapping._id);
		return Response.created(mappingUri).entity(savedMapping).build();
	}
	
	@GET
	@Timed
	@Path("/{mappingId}")
	@ApiOperation("Get the existing mapping")
	@Produces(MediaType.APPLICATION_JSON)
	public LookupMapDescription get(@ApiParam(name = "mappingId", required = true) @PathParam("mappingId") @NotEmpty String mappingId) 
			throws org.graylog2.database.NotFoundException {
		return lookupService.load(mappingId);
	}

	@PUT
	@Timed
	@Path("/{mappingId}")
	@ApiOperation(value = "Update an existing mapping", notes = "It can take up to a second until the change is applied")
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@ApiParam(name = "mappingId", required = true) @PathParam("mappingId") String mappingId, 
			@ApiParam(name = "mappingEntry", required = true) LookupMapDescription mappingEntry) 
			throws org.graylog2.database.NotFoundException, ValidationException {
		
		LookupMapDescription mapping = lookupService.load(mappingId);

		// did the mapping type change?
		if (!mapping.getClass().equals(mappingEntry.getClass())) {
			// copy the relevant fields from the saved mapping and then use the new class
			mappingEntry._id = mapping._id;
			mapping = mappingEntry;
		} else {
			// just copy the changable fields
			mapping.key = mappingEntry.key;
			mapping.value = mappingEntry.value;
			mapping.mappings = mappingEntry.mappings;
		}
		lookupService.save(mapping);
	}

	@DELETE
	@Timed
	@ApiOperation(value = "Remove the existing mapping", notes = "It can take up to a second until the change is applied")
	@Path("/{mappingId}")
	public void delete(@ApiParam(name = "mappingId", required = true) 
		@PathParam("mappingId") String mappingId) throws NotFoundException {
		
		if (lookupService.delete(mappingId) == 0) {
			throw new NotFoundException();
		}
	}

}
