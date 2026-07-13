export type ProductStatus = 'DRAFT' | 'ACTIVE' | 'INACTIVE';

export type ProductCategory =
  | 'MECHANICAL_SPARES'
  | 'ELECTRICAL_AND_LIGHTING'
  | 'EXTERIOR_ACCESSORY'
  | 'INTERIOR_ACCESSORY'
  | 'OILS_AND_LUBRICANTS'
  | 'OTHERS';

export const PRODUCT_CATEGORIES: ProductCategory[] = [
  'MECHANICAL_SPARES',
  'ELECTRICAL_AND_LIGHTING',
  'EXTERIOR_ACCESSORY',
  'INTERIOR_ACCESSORY',
  'OILS_AND_LUBRICANTS',
  'OTHERS'
];

export const CATEGORY_LABELS: Record<ProductCategory, string> = {
  MECHANICAL_SPARES: 'Mechanical Spares',
  ELECTRICAL_AND_LIGHTING: 'Electrical & Lighting',
  EXTERIOR_ACCESSORY: 'Exterior Accessory',
  INTERIOR_ACCESSORY: 'Interior Accessory',
  OILS_AND_LUBRICANTS: 'Oils & Lubricants',
  OTHERS: 'Others'
};

export interface ProductResponse {
  id: number;
  sellerId: number;
  name: string;
  description: string;
  price: number;
  category: ProductCategory;
  status: ProductStatus;
}

export interface CreateProductRequest {
  name: string;
  description?: string;
  price: number;
  category: string;
}

export interface UpdateProductRequest {
  name: string;
  description?: string;
  price: number;
  category: string;
}

export interface UpdateProductStatusRequest {
  status: ProductStatus;
}
