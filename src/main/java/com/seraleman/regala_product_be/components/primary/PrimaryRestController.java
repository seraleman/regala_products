package com.seraleman.regala_product_be.components.primary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.seraleman.regala_product_be.components.collection.Collection;
import com.seraleman.regala_product_be.components.collection.helpers.service.ICollectionService;
import com.seraleman.regala_product_be.components.element.Element;
import com.seraleman.regala_product_be.components.element.services.IElementService;
import com.seraleman.regala_product_be.components.primary.helpers.compromise.IPrimaryCompromise;
import com.seraleman.regala_product_be.components.primary.helpers.response.IPrimaryResponse;
import com.seraleman.regala_product_be.components.primary.helpers.service.IPrimaryService;
import com.seraleman.regala_product_be.helpers.localDataTime.ILocalDateTime;
import com.seraleman.regala_product_be.helpers.response.IResponse;
import com.seraleman.regala_product_be.helpers.validate.IValidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/primary")
public class PrimaryRestController {

    private static final String ENTITY = "Primary";

    @Autowired
    private ICollectionService collectionService;

    @Autowired
    private IElementService elementService;

    @Autowired
    private ILocalDateTime localDateTime;

    @Autowired
    private IPrimaryCompromise primaryCompromise;

    @Autowired
    private IPrimaryResponse primaryResponse;

    @Autowired
    private IPrimaryService primaryService;

    @Autowired
    private IResponse response;

    @Autowired
    private IValidate validate;

    @PostMapping("/")
    public ResponseEntity<?> createPrimary(
            @Valid @RequestBody Primary primary,
            BindingResult result) {

        try {
            Collection collection = collectionService
                    .getCollectionById(primary.getCollection().getId());
            if (validate.entityIsNotNull(result, collection, "collection",
                    primary.getCollection().getId()).hasErrors()) {
                return response.invalidObject(result);
            }
            LocalDateTime ldt = localDateTime.getLocalDateTime();
            primary.setCollection(collection);
            primary.setCreated(ldt);
            primary.setUpdated(ldt);
            return response.created(primaryService.savePrimary(primary));
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getPrimaries() {
        try {
            List<Primary> primaries = primaryService.getPrimaries();
            if (primaries.isEmpty()) {
                return response.empty(ENTITY);
            }
            return response.list(primaries, ENTITY);
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrimaryById(@PathVariable String id) {
        try {
            Primary primary = primaryService.getPrimaryById(id);
            if (primary == null) {
                return response.notFound(id, ENTITY);
            }
            Map<String, Object> responseCompromisedEntities = primaryCompromise
                    .deletePrimaryInCompromisedEntities(primary);

            primaryService.deletePrimaryById(id);

            return response.deletedWithCompromisedEntities(responseCompromisedEntities, ENTITY);
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPrimaryById(@PathVariable String id) {
        try {
            Primary primary = primaryService.getPrimaryById(id);
            if (primary == null) {
                return response.notFound(id, ENTITY);
            }
            return response.found(primary);
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePrimary(
            @PathVariable String id,
            @Valid @RequestBody Primary primary,
            BindingResult result) {

        try {
            Primary currentPrimary = primaryService.getPrimaryById(id);
            if (currentPrimary == null) {
                return response.notFound(id, ENTITY);
            }

            if (result.hasErrors()) {
                return response.invalidObject(result);
            }

            Collection collection = collectionService
                    .getCollectionById(primary.getCollection().getId());
            if (validate.entityIsNotNull(result, collection, "collection",
                    primary.getCollection().getId()).hasErrors()) {
                return response.invalidObject(result);
            }

            currentPrimary.setBudget(primary.getBudget());
            currentPrimary.setCollection(collection);
            currentPrimary.setName(primary.getName());
            currentPrimary.setUpdated(localDateTime.getLocalDateTime());

            return response.updatedWithCompromisedEntities(
                    primaryService.savePrimary(currentPrimary),
                    primaryCompromise.updatePrimaryInCompromisedEntities(currentPrimary),
                    ENTITY);
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

    @GetMapping("/byCollection/{collectionId}")
    public ResponseEntity<?> getPrimariesByCollectionId(@PathVariable String collectionId) {
        try {
            String searchByEntity = "Collection";
            Collection collection = collectionService.getCollectionById(collectionId);
            if (collection == null) {
                return response.cannotBeSearched(searchByEntity, collectionId);
            }
            List<Primary> primaries = (List<Primary>) primaryService
                    .getPrimariesByCollectionId(collectionId);
            if (primaries.isEmpty()) {
                return response.isNotPartOf(ENTITY, searchByEntity, collectionId);
            }
            return response.parameterizedList(primaries, ENTITY, searchByEntity, collectionId);
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

    @DeleteMapping("/delete/unusedPrimaries")
    public ResponseEntity<?> deleteUnusedPrimaries() {
        try {
            List<Primary> primaries = primaryService.getPrimaries();
            if (primaries.isEmpty()) {
                return response.empty(ENTITY);
            }

            List<Primary> undeletedPrimaries = new ArrayList<>();
            for (Primary primary : primaries) {
                List<Element> elements = elementService
                        .getElementsByPrimariesPrimaryId(primary.getId());
                if (elements.isEmpty()) {
                    primaryService.deletePrimaryById(primary.getId());
                } else {
                    undeletedPrimaries.add(primary);
                }
            }
            return response.deletedUnused(
                    primaries.size() - undeletedPrimaries.size(),
                    undeletedPrimaries,
                    ENTITY);
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

    @PostMapping("/withElement")
    public ResponseEntity<?> createPrimaryWithElement(
            @Valid @RequestBody Primary primary,
            BindingResult result) {

        try {
            Collection collection = collectionService
                    .getCollectionById(primary.getCollection().getId());
            if (validate.entityIsNotNull(result, collection, "collection",
                    primary.getCollection().getId()).hasErrors()) {
                return response.invalidObject(result);
            }
            LocalDateTime ldt = localDateTime.getLocalDateTime();
            primary.setCollection(collection);
            primary.setCreated(ldt);
            primary.setUpdated(ldt);
            Primary createdPrimary = primaryService.savePrimary(primary);

            return primaryResponse.created(
                    createdPrimary,
                    elementService.createElementFromPrimary(createdPrimary));
        } catch (DataAccessException e) {
            return response.errorDataAccess(e);
        }
    }

}
