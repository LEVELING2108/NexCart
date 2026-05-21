"use client";

import { ProductDocument } from "@/types";
import { useCartStore } from "@/store/useCartStore";

interface ProductCardProps {
  product: ProductDocument;
}

export default function ProductCard({ product }: ProductCardProps) {
  const addItem = useCartStore((state) => state.addItem);

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden hover:shadow-md transition flex flex-col h-full">
      <div className="h-48 bg-gray-100 flex items-center justify-center">
        <span className="text-gray-400 text-5xl">📦</span>
      </div>
      <div className="p-4 flex flex-col flex-grow">
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-semibold text-gray-900 line-clamp-1">{product.name}</h3>
          <span className="text-blue-600 font-bold">${product.price.toFixed(2)}</span>
        </div>
        <p className="text-sm text-gray-600 line-clamp-2 mb-4">
          {product.description}
        </p>
        <div className="flex items-center justify-between mt-auto">
          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 uppercase tracking-wider">
            {product.category}
          </span>
          <button 
            onClick={() => addItem(product)}
            className="bg-blue-600 text-white px-3 py-1.5 rounded text-sm font-medium hover:bg-blue-700 transition"
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
}
