import React from 'react';
import { Order } from '../types';
import { Clock, CheckCircle2, AlertTriangle, HelpCircle } from 'lucide-react';

interface OrderRowProps {
  order: Order;
  onSelect: (order: Order) => void;
}

export const OrderRow: React.FC<OrderRowProps> = ({ order, onSelect }) => {
  const getStatusDetails = (status: string) => {
    switch (status) {
      case 'PLACED':
        return {
          class: 'status-placed',
          label: 'Order Placed',
          icon: <Clock size={14} className="animate-pulse-glow" />,
        };
      case 'PAID':
        return {
          class: 'status-payment',
          label: 'Payment Approved',
          icon: <Clock size={14} className="animate-pulse-glow" />,
        };
      case 'KITCHEN_PREP':
        return {
          class: 'status-kitchen',
          label: 'In Kitchen',
          icon: <Clock size={14} className="animate-pulse-glow" />,
        };
      case 'OUT_FOR_DELIVERY':
        return {
          class: 'status-delivery',
          label: 'Out for Delivery',
          icon: <Clock size={14} className="animate-pulse-glow" />,
        };
      case 'DELIVERED':
        return {
          class: 'status-delivered',
          label: 'Delivered',
          icon: <CheckCircle2 size={14} />,
        };
      case 'CANCELLED':
        return {
          class: 'status-cancelled',
          label: 'Cancelled',
          icon: <AlertTriangle size={14} />,
        };
      default:
        return {
          class: '',
          label: status,
          icon: <HelpCircle size={14} />,
        };
    }
  };

  const statusInfo = getStatusDetails(order.status);

  return (
    <tr 
      onClick={() => onSelect(order)}
      className="border-b border-slate-800 hover:bg-slate-900/40 transition-all duration-150 cursor-pointer"
    >
      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-slate-300">
        #{order.orderId}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-white font-medium">
        {order.customerName}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-slate-300">
        {order.item}
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-sm text-emerald-400 font-semibold">
        ${order.amount.toFixed(2)}
      </td>
      <td className="px-6 py-4 whitespace-nowrap">
        <span className={`status-pill ${statusInfo.class}`}>
          {statusInfo.icon}
          {statusInfo.label}
        </span>
      </td>
      <td className="px-6 py-4 whitespace-nowrap text-xs text-slate-500">
        {new Date(order.createdAt).toLocaleTimeString()}
      </td>
    </tr>
  );
};
