/* DatasetsResource.java - created on Oct 30, 2014, Copyright (c) 2011 The European Library, all rights reserved */
package org.theeuropeanlibrary.repox.rest.servlets;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.dom4j.DocumentException;
import org.theeuropeanlibrary.repox.rest.pathOptions.DatasetOptionListContainer;
import org.theeuropeanlibrary.repox.rest.pathOptions.Result;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.configuration.DefaultRepoxContextUtil;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataManager;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.externalServices.ExternalRestService;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.util.exceptions.ObjectNotFoundException;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/**
 * Datasets context path handling.
 * 
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Oct 30, 2014
 */
@Path("/" + DatasetOptionListContainer.DATASETS)
@Api(value = "/" + DatasetOptionListContainer.DATASETS, description = "Rest api for datasets")
public class DatasetsResource {
    @Context
    UriInfo                   uriInfo;

    public DefaultDataManager dataManager;

    /**
     * Initialize fields before serving.
     */
    public DatasetsResource() {
        ConfigSingleton.setRepoxContextUtil(new DefaultRepoxContextUtil());
        dataManager = ((DefaultDataManager)ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager());
    }

    /**
     * Creates a new instance by providing the DataManager. (For Tests)
     * @param dataManager
     */
    public DatasetsResource(DefaultDataManager dataManager) {
        super();
        this.dataManager = dataManager;
    }

    /**
     * Retrieve all the available options for Datasets.
     * Relative path : /datasets
     * @return the list of the options available wrapped in a container
     */
    @OPTIONS
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get options over datasets conext.", httpMethod = "OPTIONS", response = DatasetOptionListContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing a list of all available options)") })
    public DatasetOptionListContainer getOptions() {
        DatasetOptionListContainer datasetOptionListContainer = new DatasetOptionListContainer(uriInfo.getBaseUri());
        return datasetOptionListContainer;
    }

    /**
     * Retrieve the dataset with the provided id.
     * Relative path : /datasets/{datasetId} 
     * @param datasetId 
     * @return Provider information
     * @throws DoesNotExistException 
     * @throws IOException 
     * @throws DocumentException 
     */
    @GET
    @Path("/" + DatasetOptionListContainer.DATASETID)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Get specific dataset.", httpMethod = "GET", response = DataSourceContainer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK (Response containing an Dataset)"),
            @ApiResponse(code = 404, message = "DoesNotExistException") })
    public DataSourceContainer getDataset(@ApiParam(value = "Id of dataset", required = true) @PathParam("datasetId") String datasetId) throws DoesNotExistException, DocumentException,
            IOException {

        DataSourceContainer datasourceContainer = null;
        datasourceContainer = dataManager.getDataSourceContainer(datasetId);
        if (datasourceContainer == null)
            throw new DoesNotExistException("Dataset with id " + datasetId + " does NOT exist!");

        return datasourceContainer;
    }

    /**
     * Create an dataset provided in the body of the post call.
     * Relative path : /datasets
     * @param providerId 
     * @param dataSourceContainer 
     * @return OK or Error Message
     * @throws MissingArgumentsException 
     * @throws AlreadyExistsException 
     * @throws InvalidArgumentsException 
     * @throws DoesNotExistException 
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ApiOperation(value = "Create a dataset.", httpMethod = "POST", response = String.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created (Response containing a String message)"),
            @ApiResponse(code = 400, message = "InvalidArgumentsException"),
            @ApiResponse(code = 404, message = "DoesNotExistException"),
            @ApiResponse(code = 406, message = "MissingArgumentsException"),
            @ApiResponse(code = 409, message = "AlreadyExistsException"),
            @ApiResponse(code = 500, message = "InternalServerErrorException") })
    public Response createDataset(@ApiParam(value = "ProviderId", required = true) @QueryParam("providerId") String providerId,
            @ApiParam(value = "Dataset data", required = true) DataSourceContainer dataSourceContainer) throws AlreadyExistsException, MissingArgumentsException, InvalidArgumentsException,
            DoesNotExistException {

        if (providerId == null || providerId.equals(""))
            throw new MissingArgumentsException("Missing argument providerId!");

        if (dataSourceContainer instanceof DefaultDataSourceContainer)
        {
            DefaultDataSourceContainer defaultDataSourceContainer = (DefaultDataSourceContainer)dataSourceContainer;
            DataSource dataSource = defaultDataSourceContainer.getDataSource();
            String name = defaultDataSourceContainer.getName();
            String nameCode = defaultDataSourceContainer.getNameCode();

            String id = dataSource.getId();
            String description = dataSource.getDescription();
            String exportPath = dataSource.getExportDir();
            String schema = dataSource.getSchema();
            String namespace = dataSource.getNamespace();
            String metadataFormat = dataSource.getMetadataFormat();
            String marcFormat = dataSource.getMarcFormat();
            Map<String, MetadataTransformation> metadataTransformations = null;
            List<ExternalRestService> externalRestServices = null;
            
            if (id == null || id.isEmpty())
                throw new MissingArgumentsException("Invalid value: " + "Dataset id must not be empty");
            else if (schema == null || schema.equals(""))
                throw new MissingArgumentsException("Invalid value: " + "Dataset schema must not be empty");
            else if (namespace == null || namespace.equals(""))
                throw new MissingArgumentsException("Invalid value: " + "Dataset namespace must not be empty");

            if (dataSource instanceof OaiDataSource)
            {
                OaiDataSource oaiDataSource = (OaiDataSource)dataSource;
                String oaiSourceURL = oaiDataSource.getOaiSourceURL();
                String oaiSet = oaiDataSource.getOaiSet();
                
                if (oaiSourceURL == null || oaiSourceURL.isEmpty())
                    throw new MissingArgumentsException("Invalid value: " + "Dataset oaiSourceURL must not be empty");
                else if (oaiSet == null || oaiSet.equals(""))
                    throw new MissingArgumentsException("Invalid value: " + "Dataset oaiSet must not be empty");

                try {
                    dataManager.createDataSourceOai(providerId, id, description, nameCode, name, exportPath, schema, namespace, metadataFormat,
                            oaiSourceURL, oaiSet, metadataTransformations,
                            externalRestServices, marcFormat);
                } catch (InvalidArgumentsException e) {
                    throw new InvalidArgumentsException("Invalid value: " + e.getMessage());
                } catch (ObjectNotFoundException e) {
                    throw new DoesNotExistException("Provider with id " + e.getMessage() + " does NOT exist!");
                } catch (AlreadyExistsException e) {
                    throw new AlreadyExistsException("Dataset with id " + e.getMessage() + " already exists!");
                } catch (SQLException | DocumentException | IOException e) {
                    throw new InternalServerErrorException("Error in server : " + e.getMessage());
                }
            }
            return Response.created(null).entity(new Result("DataProvider with id = " + id + " and name = " + name + " created successfully")).build();
        }
        return null;
    }

}
