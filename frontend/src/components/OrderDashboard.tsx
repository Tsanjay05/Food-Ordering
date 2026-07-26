import React from 'react';
import { Order } from '../types';
import { OrderRow } from './OrderRow';
import { RefreshCw, LayoutDashboard } from 'lucide-react';

interface OrderDashboardProps {
  orders: Order[];
  onRefresh: () => void;
  onSelect: (order: Order) => void;
  loading: boolean;
}

export const OrderDashboard: React.FC<OrderDashboardProps> = ({ orders, onRefresh, onSelect, loading }) => {
  return (
    <div className="glass-panel p-6 flex flex-col gap-4">
      <div className="flex justify-between items-center mb-2">
        <h2 className="text-xl font-semibold flex items-center gap-2 text-indigo-400">
          <LayoutDashboard size={22} /> Order Live Operations Room
        </h2>
        <button
          onClick={onRefresh}
          disabled={loading}
          className="p-2 rounded-lg bg-slate-800 border border-slate-700 hover:bg-slate-700 text-slate-300 hover:text-white transition-all disabled:opacity-50 flex items-center gap-1.5 text-xs font-semibold"
        >
          <RefreshCw size={14} className={loading ? 'animate-spin' : ''} />
          Refresh
        </button>
      </div>

      <div className="overflow-x-auto rounded-lg border border-slate-800">
        <table className="min-w-full divide-y divide-slate-800 bg-slate-950/20">
          <thead>
            <tr className="bg-slate-900/50">
              <th className="px-6 py-3.5 text-left text-xs font-bold text-slate-400 uppercase tracking-wider">
                ID
              </th>
              <th className="px-6 py-3.5 text-left text-xs font-bold text-slate-400 uppercase tracking-wider">
                Customer
              </th>
              <th className="px-6 py-3.5 text-left text-xs font-bold text-slate-400 uppercase tracking-wider">
                Gourmet Selection
              </th>
              <th className="px-6 py-3.5 text-left text-xs font-bold text-slate-400 uppercase tracking-wider">
                Value
              </th>
              <th className="px-6 py-3.5 text-left text-xs font-bold text-slate-400 uppercase tracking-wider">
                Lifecycle State
              </th>
              <th className="px-6 py-3.5 text-left text-xs font-bold text-slate-400 uppercase tracking-wider">
                Placed At
              </th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800">
            {orders.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-6 py-12 text-center text-slate-500 text-sm">
                  No active orders on the dashboard. Use the order form to generate transactions.
                </td>
              </tr>
            ) : (
              orders.map((order) => <OrderRow key={order.orderId} order={order} onSelect={onSelect} />)
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
