/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.sale.service.cartline;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.sale.db.Cart;
import com.axelor.apps.sale.db.CartLine;
import com.axelor.apps.sale.db.repo.CartLineRepository;
import com.axelor.apps.sale.service.saleorderline.product.SaleOrderLineProductService;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;

public class CartLineCreateServiceImpl implements CartLineCreateService {

  protected ProductRepository productRepository;
  protected SaleOrderLineProductService saleOrderLineProductService;
  protected CartLinePriceService cartLinePriceService;
  protected CartLineRepository cartLineRepository;
  protected CartLineProductService cartLineProductService;

  @Inject
  public CartLineCreateServiceImpl(
      ProductRepository productRepository,
      SaleOrderLineProductService saleOrderLineProductService,
      CartLinePriceService cartLinePriceService,
      CartLineRepository cartLineRepository,
      CartLineProductService cartLineProductService) {
    this.productRepository = productRepository;
    this.saleOrderLineProductService = saleOrderLineProductService;
    this.cartLinePriceService = cartLinePriceService;
    this.cartLineRepository = cartLineRepository;
    this.cartLineProductService = cartLineProductService;
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public CartLine createCartLine(Cart cart, Product product, BigDecimal qty)
      throws AxelorException {
    if (qty == null) {
      qty = BigDecimal.ONE;
    }
    CartLine cartLine = new CartLine();
    cartLine.setProduct(productRepository.find(product.getId()));
    cartLine.setQty(qty);
    cartLine.setUnit(saleOrderLineProductService.getSaleUnit(product));
    cartLine.setPrice(
        cartLinePriceService.getSalePrice(product, cart.getCompany(), cart.getPartner()));
    cartLine.setCart(cart);
    return cartLineRepository.save(cartLine);
  }

  @Override
  public CartLine createCartLine(Cart cart, Product product) throws AxelorException {
    return createCartLine(cart, product, null);
  }

  @Override
  @Transactional(rollbackOn = Exception.class)
  public CartLine createCartLineWithVariant(Cart cart, Product product, BigDecimal qty)
      throws AxelorException {
    if (qty == null) {
      qty = BigDecimal.ONE;
    }
    CartLine cartLine = new CartLine();
    cartLine.setProduct(product.getParentProduct());
    cartLine.setVariantProduct(product);
    cartLine.setQty(qty);
    cartLineProductService.getProductInformation(cart, cartLine);
    cartLine.setCart(cart);
    return cartLineRepository.save(cartLine);
  }
}
