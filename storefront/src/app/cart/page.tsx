"use client";

import Link from 'next/link';
import { useCartStore } from "@/store/useCartStore";
import { useEffect, useState } from "react";

export default function CartPage() {
  const { items, removeItem, updateQuantity, getTotal, clearCart } = useCartStore();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) return null;

  return (
    <div className="max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Shopping Cart</h1>

      {items.length > 0 ? (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-4">
            {items.map((item) => (
              <div key={item.id} className="bg-white p-4 rounded-lg shadow-sm border border-gray-200 flex items-center gap-4">
                <div className="h-20 w-20 bg-gray-100 rounded flex items-center justify-center text-3xl">
                  📦
                </div>
                <div className="flex-grow">
                  <h3 className="font-semibold text-gray-900">{item.name}</h3>
                  <p className="text-sm text-gray-500 line-clamp-1">{item.category}</p>
                  <div className="flex items-center gap-4 mt-2">
                    <div className="flex items-center border border-gray-300 rounded">
                      <button 
                        onClick={() => updateQuantity(item.id, Math.max(1, item.quantity - 1))}
                        className="px-2 py-1 hover:bg-gray-100"
                      >
                        -
                      </button>
                      <span className="px-3 py-1 text-sm font-medium">{item.quantity}</span>
                      <button 
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                        className="px-2 py-1 hover:bg-gray-100"
                      >
                        +
                      </button>
                    </div>
                    <button 
                      onClick={() => removeItem(item.id)}
                      className="text-red-600 text-sm hover:underline"
                    >
                      Remove
                    </button>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-bold text-gray-900">${(item.price * item.quantity).toFixed(2)}</p>
                  <p className="text-xs text-gray-500">${item.price.toFixed(2)} each</p>
                </div>
              </div>
            ))}
            
            <button 
              onClick={clearCart}
              className="text-gray-500 text-sm hover:text-gray-700"
            >
              Clear Cart
            </button>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 h-fit">
            <h2 className="text-xl font-bold mb-4">Order Summary</h2>
            <div className="space-y-2 border-b border-gray-100 pb-4 mb-4">
              <div className="flex justify-between text-gray-600">
                <span>Subtotal</span>
                <span>${getTotal().toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-gray-600">
                <span>Shipping</span>
                <span className="text-green-600 font-medium">Free</span>
              </div>
            </div>
            <div className="flex justify-between text-lg font-bold mb-6">
              <span>Total</span>
              <span className="text-blue-600">${getTotal().toFixed(2)}</span>
            </div>
            <Link 
              href="/checkout"
              className="block w-full bg-blue-600 text-white text-center py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
            >
              Proceed to Checkout
            </Link>
          </div>
        </div>
      ) : (
        <div className="text-center py-20 bg-white rounded-xl shadow-sm border border-gray-100">
          <div className="text-6xl mb-4 text-gray-300">🛒</div>
          <p className="text-xl text-gray-500 mb-8">Your cart is empty.</p>
          <Link 
            href="/products"
            className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
          >
            Start Shopping
          </Link>
        </div>
      )}
    </div>
  );
}
