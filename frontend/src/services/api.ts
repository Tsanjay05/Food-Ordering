import axios from 'axios';
import { Order, OrderCreateRequest } from '../types';

const client = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const api = {
  createOrder: async (request: OrderCreateRequest): Promise<Order> => {
    const response = await client.post<Order>('/orders', request);
    return response.data;
  },

  getOrders: async (): Promise<Order[]> => {
    const response = await client.get<Order[]>('/orders');
    return response.data;
  },

  getOrderById: async (id: number): Promise<Order> => {
    const response = await client.get<Order>(`/orders/${id}`);
    return response.data;
  },
};
