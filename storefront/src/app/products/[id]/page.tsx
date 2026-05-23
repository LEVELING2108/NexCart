"use client";

import { useEffect, useState, use } from "react";
import api from "@/lib/axios";
import { ApiResponse, ProductResponse } from "@/types";
import { useCartStore } from "@/store/useCartStore";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function ProductDetailsPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = use(params);
  const [product, setProduct] = useState<ProductResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const addItem = useCartStore((state) => state.addItem);
  const router = useRouter();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await api.get<ApiResponse<ProductResponse>>(`/products/${id}`);
        if (response.data.success) {
          setProduct(response.data.data);
        }
      } catch (error) {
        console.error("Failed to fetch product", error);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-20 animate-pulse">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
          <div className="aspect-square bg-gray-200 rounded-2xl" />
          <div className="space-y-6">
            <div className="h-10 bg-gray-200 rounded w-3/4" />
            <div className="h-6 bg-gray-200 rounded w-1/4" />
            <div className="h-32 bg-gray-200 rounded w-full" />
            <div className="h-12 bg-gray-200 rounded w-1/2" />
          </div>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="text-center py-20">
        <h2 className="text-2xl font-bold text-gray-900">Product Not Found</h2>
        <p className="text-gray-600 mt-2">The product you're looking for doesn't exist or has been removed.</p>
        <Link href="/products" className="mt-6 inline-block text-blue-600 hover:underline">
          Back to Products
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-12">
      <button 
        onClick={() => router.back()}
        className="mb-8 text-gray-500 hover:text-gray-700 flex items-center gap-2 transition"
      >
        ← Back
      </button>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-start">
        <div className="aspect-square bg-white rounded-2xl shadow-sm border border-gray-100 flex items-center justify-center text-9xl">
          📦
        </div>

        <div className="space-y-8">
          <div>
            <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800 uppercase tracking-wider mb-4">
              {product.categoryName}
            </span>
            <h1 className="text-4xl font-extrabold text-gray-900">{product.name}</h1>
            <p className="text-3xl font-bold text-blue-600 mt-4">${product.price.toFixed(2)}</p>
          </div>

          <div className="prose prose-blue">
            <h3 className="text-lg font-bold text-gray-900">Description</h3>
            <p className="text-gray-600 leading-relaxed">
              {product.description}
            </p>
          </div>

          <div className="pt-8 border-t border-gray-100 space-y-4">
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-500">Availability</span>
              <span className={`font-semibold ${product.stockQuantity > 0 ? 'text-green-600' : 'text-red-600'}`}>
                {product.stockQuantity > 0 ? `${product.stockQuantity} in stock` : 'Out of Stock'}
              </span>
            </div>
            
            <button 
              onClick={() => {
                addItem({
                    id: product.id,
                    name: product.name,
                    price: product.price,
                    description: product.description,
                    category: product.categoryName
                });
                router.push('/cart');
              }}
              disabled={product.stockQuantity <= 0}
              className={`w-full py-4 rounded-xl font-bold text-white transition shadow-lg ${
                product.stockQuantity > 0 
                  ? 'bg-blue-600 hover:bg-blue-700' 
                  : 'bg-gray-400 cursor-not-allowed'
              }`}
            >
              {product.stockQuantity > 0 ? 'Add to Cart' : 'Out of Stock'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
