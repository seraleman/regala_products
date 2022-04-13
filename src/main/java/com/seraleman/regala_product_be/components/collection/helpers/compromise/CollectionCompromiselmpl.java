package com.seraleman.regala_product_be.components.collection.helpers.compromise;

import java.util.LinkedHashMap;
import java.util.Map;

import com.seraleman.regala_product_be.components.collection.Collection;
import com.seraleman.regala_product_be.helpers.structure.IStructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionCompromiselmpl implements ICollectionCompromise {

        @Autowired
        private ICollectionCompromisedEntities collectionCompromisedEntities;

        @Autowired
        private IStructure structure;

        @Override
        public Map<String, Object> updateCollectionInCompromisedEntities(Collection collection) {

                Map<String, Object> response = new LinkedHashMap<>();

                response.put("primaries", structure.responseUpdatedCompromisedEntities(
                                collectionCompromisedEntities
                                                .updateCollectionInCompromisedPrimaries(
                                                                collection)));
                collectionCompromisedEntities
                                .updateCollectionOfPrimariesInCompromisedElements(collection);

                response.put("elements", structure.responseUpdatedCompromisedEntities(
                                collectionCompromisedEntities
                                                .updateCollectionInCompromisedElements(
                                                                collection)));
                collectionCompromisedEntities
                                .updateCollectionOfPrimariesOfElementsInCompromisedGifts(collection);

                response.put("gifts", structure.responseUpdatedCompromisedEntities(
                                collectionCompromisedEntities
                                                .updateCollectionOfElementsInCompromisedGifts(
                                                                collection)));
                return response;
        }

}
