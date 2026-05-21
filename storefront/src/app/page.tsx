import Link from 'next/link';

export default function Home() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] text-center">
      <h1 className="text-4xl sm:text-6xl font-extrabold text-gray-900 mb-6">
        Welcome to <span className="text-blue-600">NexCart</span>
      </h1>
      <p className="text-lg sm:text-xl text-gray-600 max-w-2xl mb-10">
        Experience the next generation of e-commerce. Built with high-performance microservices and reliable event-driven architecture.
      </p>
      <div className="flex flex-col sm:flex-row gap-4">
        <Link
          href="/products"
          className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold text-lg hover:bg-blue-700 transition shadow-lg"
        >
          Shop Now
        </Link>
        <Link
          href="/about"
          className="bg-white text-blue-600 border-2 border-blue-600 px-8 py-3 rounded-lg font-semibold text-lg hover:bg-blue-50 transition"
        >
          Learn More
        </Link>
      </div>
      
      <div className="mt-20 grid grid-cols-1 sm:grid-cols-3 gap-8 w-full max-w-5xl text-left">
        <div className="p-6 bg-white rounded-xl shadow-sm border border-gray-100">
          <h3 className="font-bold text-xl mb-3 text-gray-800">Reliable Delivery</h3>
          <p className="text-gray-600">Guaranteed order processing via our distributed Saga orchestration.</p>
        </div>
        <div className="p-6 bg-white rounded-xl shadow-sm border border-gray-100">
          <h3 className="font-bold text-xl mb-3 text-gray-800">Secure Payments</h3>
          <p className="text-gray-600">Professional-grade security powered by Keycloak IAM and OAuth2.</p>
        </div>
        <div className="p-6 bg-white rounded-xl shadow-sm border border-gray-100">
          <h3 className="font-bold text-xl mb-3 text-gray-800">Lightning Search</h3>
          <p className="text-gray-600">Find what you need instantly with our Elasticsearch-backed engine.</p>
        </div>
      </div>
    </div>
  );
}
