"use client";

import { useEffect, useState } from "react";
import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useCartStore } from "@/store/useCartStore";
import api from "@/lib/axios";
import { ApiResponse, OrderRequest, OrderResponse } from "@/types";

export default function CheckoutPage() {
  const { status, data: session } = useSession();
  const router = useRouter();
  const { items, getTotal, clearCart } = useCartStore();
  const [loading, setLoading] = useState(false);
  const [orderConfirmed, setOrderConfirmed] = useState<OrderResponse | null>(null);

  useEffect(() => {
    if (status === "unauthenticated") {
      router.push("/api/auth/signin");
    }
  }, [status, router]);

  const handlePlaceOrder = async () => {
    if (!session || items.length === 0) return;

    setLoading(true);
    const orderRequest: OrderRequest = {
      items: items.map((item) => ({
        productId: item.id,
        quantity: item.quantity,
        price: item.price,
      })),
    };

    try {
      const response = await api.post<ApiResponse<OrderResponse>>("/orders", orderRequest, {
        headers: {
          Authorization: `Bearer ${session.accessToken}`,
        },
      });
      
      if (response.data.success) {
        setOrderConfirmed(response.data.data);
        clearCart();
      }
    } catch (error) {
      console.error("Failed to place order", error);
      alert("Failed to place order. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  if (status === "loading") return <div className="text-center py-20">Loading...</div>;

  if (orderConfirmed) {
    return (
      <div className="max-w-2xl mx-auto text-center py-20">
        <div className="text-6xl mb-6 text-green-500">🎉</div>
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Order Confirmed!</h1>
        <p className="text-gray-600 mb-8">
          Thank you for your purchase. Your order ID is <span className="font-mono font-bold">{orderConfirmed.id}</span>.
        </p>
        <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 mb-8 text-left">
          <h3 className="font-bold mb-2">Next Steps</h3>
          <ul className="text-sm text-gray-600 space-y-2 list-disc list-inside">
            <li>Our system is currently reserving your items.</li>
            <li>You will receive a notification once payment is processed.</li>
            <li>Track your order status in your dashboard.</li>
          </ul>
        </div>
        <button 
          onClick={() => router.push("/products")}
          className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
        >
          Back to Shop
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Checkout</h1>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
        <div className="space-y-8">
          <div>
            <h2 className="text-xl font-bold mb-4 border-b pb-2">Shipping Information</h2>
            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                <input type="text" className="w-full px-4 py-2 border rounded-md" defaultValue={session?.user?.name || ""} />
              </div>
              <div className="col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                <input type="email" className="w-full px-4 py-2 border rounded-md" defaultValue={session?.user?.email || ""} readOnly />
              </div>
              <div className="col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">Address</label>
                <input type="text" className="w-full px-4 py-2 border rounded-md" placeholder="123 Main St" />
              </div>
            </div>
          </div>

          <div>
            <h2 className="text-xl font-bold mb-4 border-b pb-2">Payment (Simulation)</h2>
            <p className="text-sm text-gray-500 mb-4 italic">
              Payment is handled asynchronously by our Payment Service after order placement.
            </p>
            <div className="p-4 bg-blue-50 text-blue-800 rounded-md text-sm border border-blue-100">
              ℹ️ In this prototype, we'll process a simulated payment upon order confirmation.
            </div>
          </div>
        </div>

        <div className="bg-gray-50 p-6 rounded-xl border border-gray-200 h-fit">
          <h2 className="text-xl font-bold mb-4">Your Order</h2>
          <div className="space-y-4 mb-6 max-h-60 overflow-y-auto">
            {items.map((item) => (
              <div key={item.id} className="flex justify-between text-sm">
                <span>{item.name} x {item.quantity}</span>
                <span className="font-medium">${(item.price * item.quantity).toFixed(2)}</span>
              </div>
            ))}
          </div>
          <div className="border-t pt-4 space-y-2">
            <div className="flex justify-between text-lg font-bold">
              <span>Total</span>
              <span className="text-blue-600">${getTotal().toFixed(2)}</span>
            </div>
          </div>
          <button 
            onClick={handlePlaceOrder}
            disabled={loading || items.length === 0}
            className={`w-full mt-8 py-4 rounded-lg font-bold text-white transition shadow-lg ${
              loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'
            }`}
          >
            {loading ? "Processing..." : `Place Order - $${getTotal().toFixed(2)}`}
          </button>
        </div>
      </div>
    </div>
  );
}
