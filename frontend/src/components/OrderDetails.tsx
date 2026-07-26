import React from 'react';
import { Order } from '../types';
import { CreditCard, ChefHat, Truck, CheckCircle2, XCircle, ArrowLeft, ClipboardList } from 'lucide-react';

interface OrderDetailsProps {
  order: Order;
  onBack: () => void;
}

export const OrderDetails: React.FC<OrderDetailsProps> = ({ order, onBack }) => {
  const steps = [
    { status: 'PLACED', label: 'Order Placed', icon: <ClipboardList size={20} /> },
    { status: 'PAID', label: 'Payment', icon: <CreditCard size={20} /> },
    { status: 'KITCHEN_PREP', label: 'Kitchen Prep', icon: <ChefHat size={20} /> },
    { status: 'OUT_FOR_DELIVERY', label: 'Out for Delivery', icon: <Truck size={20} /> },
    { status: 'DELIVERED', label: 'Delivered', icon: <CheckCircle2 size={20} /> },
  ];

  const getStepIndex = (status: string) => {
    if (status === 'CANCELLED') return 1; // Stopped at payment
    if (status === 'PAYMENT') return 1;
    if (status === 'PAID') return 1;
    if (status === 'KITCHEN_PREP') return 2;
    if (status === 'OUT_FOR_DELIVERY') return 3;
    if (status === 'DELIVERED') return 4;
    return 0; // PLACED
  };

  const currentStepIndex = getStepIndex(order.status);
  const isCancelled = order.status === 'CANCELLED';

  return (
    <div className="glass-panel p-6 flex flex-col gap-6">
      <div className="flex items-center gap-3 border-b border-slate-800 pb-4">
        <button
          onClick={onBack}
          className="p-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-all"
        >
          <ArrowLeft size={16} />
        </button>
        <div>
          <h2 className="text-xl font-bold text-white">Order Details #{order.orderId}</h2>
          <p className="text-xs text-slate-400">Placed on {new Date(order.createdAt).toLocaleString()}</p>
        </div>
      </div>

      {/* Stepper Progress */}
      <div className="flex flex-col md:flex-row justify-between items-center gap-4 py-4 px-2 bg-slate-950/30 rounded-xl border border-slate-800/50">
        {steps.map((step, index) => {
          const isActive = index <= currentStepIndex && !isCancelled;
          const isCurrent = index === currentStepIndex && !isCancelled;
          const isPast = index < currentStepIndex && !isCancelled;

          return (
            <React.Fragment key={step.status}>
              {index > 0 && (
                <div
                  className={`hidden md:block flex-1 h-0.5 min-w-[20px] transition-all duration-500 ${
                    index <= currentStepIndex && !isCancelled ? 'bg-indigo-500 shadow-[0_0_8px_#6366f1]' : 'bg-slate-800'
                  }`}
                />
              )}
              <div className="flex flex-row md:flex-col items-center gap-2.5 md:gap-2">
                <div
                  className={`w-10 h-10 rounded-full flex items-center justify-center transition-all duration-300 ${
                    isCurrent
                      ? 'bg-indigo-600 text-white border-2 border-indigo-400 shadow-[0_0_15px_#6366f1] animate-pulse-glow'
                      : isPast
                      ? 'bg-emerald-600 text-white'
                      : isActive
                      ? 'bg-indigo-900 text-indigo-200'
                      : 'bg-slate-900 text-slate-500 border border-slate-800'
                  }`}
                >
                  {step.icon}
                </div>
                <span
                  className={`text-xs font-semibold tracking-wider ${
                    isCurrent ? 'text-indigo-400' : isPast ? 'text-emerald-400' : 'text-slate-500'
                  }`}
                >
                  {step.label}
                </span>
              </div>
            </React.Fragment>
          );
        })}

        {isCancelled && (
          <>
            <div className="hidden md:block flex-1 h-0.5 bg-slate-800" />
            <div className="flex flex-row md:flex-col items-center gap-2">
              <div className="w-10 h-10 rounded-full bg-rose-950 border border-rose-500 text-rose-400 flex items-center justify-center shadow-[0_0_12px_rgba(244,63,94,0.3)]">
                <XCircle size={20} />
              </div>
              <span className="text-xs font-semibold text-rose-400 tracking-wider">Cancelled</span>
            </div>
          </>
        )}
      </div>

      {/* Details Box */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 bg-slate-950/20 p-5 rounded-xl border border-slate-800">
        <div className="flex flex-col gap-3">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider">Customer & Selection</h3>
          <div>
            <p className="text-sm text-slate-500">Customer Name</p>
            <p className="text-base text-white font-medium">{order.customerName}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">Item Ordered</p>
            <p className="text-base text-white font-medium">{order.item}</p>
          </div>
        </div>

        <div className="flex flex-col gap-3">
          <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider">Payment & Status</h3>
          <div>
            <p className="text-sm text-slate-500">Total Price</p>
            <p className="text-lg text-emerald-400 font-bold">${order.amount.toFixed(2)}</p>
          </div>
          <div>
            <p className="text-sm text-slate-500">Current Status</p>
            <span className={`status-pill mt-1 inline-flex`}>
              {order.status}
            </span>
          </div>
        </div>
      </div>

      {/* Operational Logs Mock */}
      <div className="flex flex-col gap-3">
        <h3 className="text-sm font-bold text-slate-400 uppercase tracking-wider">Operational Logs</h3>
        <div className="bg-slate-950 p-4 rounded-xl border border-slate-800 font-mono text-xs text-slate-400 flex flex-col gap-2 max-h-[180px] overflow-y-auto">
          <div><span className="text-slate-500">[System]</span> Order #{order.orderId} successfully initialized</div>
          <div><span className="text-indigo-400">[OrderService]</span> Order status set to <span className="text-indigo-300">PLACED</span></div>
          <div><span className="text-yellow-400">[ActiveMQ]</span> Dispatched event `order.created` for Order #{order.orderId}</div>
          
          {currentStepIndex >= 1 && (
            <>
              <div><span className="text-purple-400">[Camunda]</span> Consumed ActiveMQ event. Initiating Workflow Process</div>
              <div><span className="text-indigo-400">[OrderService]</span> Triggered CallPaymentDelegate</div>
              <div><span className="text-green-400">[PaymentService]</span> Processed payment check for Order #{order.orderId}</div>
              {isCancelled ? (
                <>
                  <div className="text-rose-400"><span className="text-rose-500">[PaymentService]</span> Payment failed. Transaction rejected.</div>
                  <div className="text-rose-400"><span className="text-indigo-400">[OrderService]</span> Update status to CANCELLED. Workflow aborted.</div>
                </>
              ) : (
                <>
                  <div><span className="text-green-400">[PaymentService]</span> Transaction status: <span className="text-emerald-400">SUCCESS</span> (TXN-{order.orderId}B9)</div>
                  <div><span className="text-indigo-400">[OrderService]</span> Update status to PAID</div>
                </>
              )}
            </>
          )}

          {currentStepIndex >= 2 && (
            <>
              <div><span className="text-indigo-400">[OrderService]</span> Triggered CallKitchenDelegate</div>
              <div><span className="text-blue-400">[KitchenService]</span> Ticket received for {order.item}</div>
              <div><span className="text-blue-400">[KitchenService]</span> Food preparation complete: <span className="text-blue-300">READY</span></div>
              <div><span className="text-indigo-400">[OrderService]</span> Update status to KITCHEN_PREP</div>
            </>
          )}

          {currentStepIndex >= 3 && (
            <>
              <div><span className="text-indigo-400">[OrderService]</span> Triggered CallDeliveryDelegate</div>
              <div><span className="text-purple-400">[DeliveryService]</span> Assigning driver to Order #{order.orderId}</div>
              <div><span className="text-purple-400">[DeliveryService]</span> Courier assigned: <span className="text-purple-300">Speedy Courier</span></div>
              <div><span className="text-indigo-400">[OrderService]</span> Update status to OUT_FOR_DELIVERY</div>
            </>
          )}

          {currentStepIndex >= 4 && (
            <>
              <div><span className="text-indigo-400">[OrderService]</span> Triggered CompleteWorkflowDelegate</div>
              <div className="text-emerald-400"><span className="text-indigo-400">[OrderService]</span> Order #{order.orderId} - Workflow COMPLETE</div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};
