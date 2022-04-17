package com.seraleman.regala_product_be.components.primary.helpers.compromise;

import java.util.List;

import com.seraleman.regala_product_be.components.element.Element;
import com.seraleman.regala_product_be.components.gift.Gift;
import com.seraleman.regala_product_be.components.primary.Primary;

public interface IPrimaryCompromisedEntities {

    List<Element> deletePrimaryInCompromisedElements(Primary primary);

    List<Gift> deletePrimaryOfElementsInCompromisedGifts(Primary primary);

    List<Element> updatePrimaryInCompromisedElements(Primary primary);

    List<Gift> updatePrimaryOfElementsInCompromisedGifts(Primary primary);

}
