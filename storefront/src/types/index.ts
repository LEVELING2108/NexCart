export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface ProductResponse {
  id: string;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  categoryName: string;
}

export interface ProductDocument {
  id: string;
  name: string;
  description: string;
  price: number;
  category: string;
}

export interface OrderRequest {
  items: {
    productId: string;
    quantity: number;
    price: number;
  }[];
}

export interface OrderResponse {
  id: string;
  status: string;
  totalAmount: number;
}
