export type OrderStatus =
  | 'PLACED'
  | 'PAID'
  | 'KITCHEN_PREP'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'CANCELLED';

export interface Order {
  orderId: number;
  customerName: string;
  item: string;
  amount: number;
  status: OrderStatus;
  createdAt: string;
  updatedAt?: string;
}

export interface OrderCreateRequest {
  customerName: string;
  item: string;
  amount: number;
}
