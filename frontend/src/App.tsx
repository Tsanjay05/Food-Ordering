import { useEffect, useState, useCallback } from 'react';
import { OrderForm } from './components/OrderForm';
import { OrderDashboard } from './components/OrderDashboard';
import { OrderDetails } from './components/OrderDetails';
import { Order, OrderCreateRequest } from './types';
import { api } from './services/api';
import { ChefHat } from 'lucide-react';

function App() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [refreshLoading, setRefreshLoading] = useState(false);
  const [submitLoading, setSubmitLoading] = useState(false);

  const fetchOrders = useCallback(async () => {
    setRefreshLoading(true);
    try {
      const data = await api.getOrders();
      setOrders(data);
      
      // Update selectedOrder if it is currently open
      if (selectedOrder) {
        const updated = data.find(o => o.orderId === selectedOrder.orderId);
        if (updated) {
          setSelectedOrder(updated);
        }
      }
    } catch (err) {
      console.error('Failed to retrieve orders list', err);
    } finally {
      setRefreshLoading(false);
    }
  }, [selectedOrder]);

  const handleCreateOrder = async (request: OrderCreateRequest) => {
    setSubmitLoading(true);
    try {
      const newOrder = await api.createOrder(request);
      setOrders((prev) => [newOrder, ...prev]);
    } catch (err) {
      console.error('Failed to request new order', err);
    } finally {
      setSubmitLoading(false);
    }
  };

  // Poll orders list every 2 seconds for real-time status tracking
  useEffect(() => {
    fetchOrders();
    const interval = setInterval(() => {
      fetchOrders();
    }, 2000);
    return () => clearInterval(interval);
  }, [fetchOrders]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 flex flex-col gap-8 min-h-screen">
      <header className="flex justify-between items-center py-4 border-b border-slate-800">
        <h1 className="text-3xl font-extrabold flex items-center gap-3 bg-gradient-to-r from-indigo-400 to-violet-400 bg-clip-text text-transparent">
          <ChefHat size={36} className="text-indigo-400" />
          Gourmet Express
        </h1>
        <div className="text-right">
          <p className="text-xs text-slate-500 font-semibold uppercase tracking-wider">System Orchestration Console</p>
          <p className="text-sm text-emerald-400 font-medium flex items-center gap-1.5 justify-end">
            <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse"></span>
            Operational (REST + ActiveMQ)
          </p>
        </div>
      </header>

      <main className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <section className="lg:col-span-1">
          <OrderForm onSubmit={handleCreateOrder} loading={submitLoading} />
        </section>

        <section className="lg:col-span-2">
          {selectedOrder ? (
            <OrderDetails order={selectedOrder} onBack={() => setSelectedOrder(null)} />
          ) : (
            <OrderDashboard 
              orders={orders} 
              onRefresh={fetchOrders} 
              onSelect={setSelectedOrder} 
              loading={refreshLoading} 
            />
          )}
        </section>
      </main>

      <footer className="mt-auto py-6 border-t border-slate-800 text-center text-xs text-slate-600">
        <p>© 2026 Gourmet Express Operations Center. Process orchestration managed by Camunda BPM engine.</p>
      </footer>
    </div>
  );
}

export default App;
