import React, { useState } from 'react';
import { OrderCreateRequest } from '../types';
import { PlusCircle } from 'lucide-react';

interface OrderFormProps {
  onSubmit: (data: OrderCreateRequest) => Promise<void>;
  loading: boolean;
}

export const OrderForm: React.FC<OrderFormProps> = ({ onSubmit, loading }) => {
  const [customerName, setCustomerName] = useState('');
  const [item, setItem] = useState('Truffle Mushroom Burger');
  const [amount, setAmount] = useState('18.50');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerName.trim() || !item || !amount) return;

    try {
      await onSubmit({
        customerName: customerName.trim(),
        item,
        amount: parseFloat(amount),
      });
      setCustomerName('');
    } catch (err) {
      console.error('Failed to submit order', err);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="glass-panel p-6 flex flex-col gap-4">
      <h2 className="text-xl font-semibold mb-2 flex items-center gap-2 text-indigo-400">
        <PlusCircle size={22} /> Place New Gourmet Order
      </h2>

      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-400">Customer Name</label>
        <input
          type="text"
          value={customerName}
          onChange={(e) => setCustomerName(e.target.value)}
          placeholder="e.g. Alice Vance"
          className="bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:outline-none focus:border-indigo-500"
          required
          disabled={loading}
        />
      </div>

      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-400">Select Item</label>
        <select
          value={item}
          onChange={(e) => setItem(e.target.value)}
          className="bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:outline-none focus:border-indigo-500"
          disabled={loading}
        >
          <option value="Truffle Mushroom Burger">Truffle Mushroom Burger - $18.50</option>
          <option value="Spicy Cajun Chicken Pizza">Spicy Cajun Chicken Pizza - $22.00</option>
          <option value="Avocado Quinoa Power Salad">Avocado Quinoa Power Salad - $15.50</option>
          <option value="Belgian Chocolate Waffles">Belgian Chocolate Waffles - $12.75</option>
        </select>
      </div>

      <div className="flex flex-col gap-1">
        <label className="text-sm font-medium text-slate-400">Amount ($)</label>
        <input
          type="number"
          step="0.01"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          className="bg-slate-900 border border-slate-700 rounded-lg p-2.5 text-white focus:outline-none focus:border-indigo-500"
          required
          disabled
        />
      </div>

      <button
        type="submit"
        disabled={loading || !customerName.trim()}
        className="w-full mt-2 bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500 text-white font-semibold py-2.5 px-4 rounded-lg transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 shadow-lg shadow-indigo-500/20"
      >
        {loading ? 'Processing...' : 'Place Order'}
      </button>
    </form>
  );
};
