import Link from 'next/link';

export default function AboutPage() {
  return (
    <div className="max-w-4xl mx-auto py-12 px-4">
      <h1 className="text-4xl font-bold text-gray-900 mb-8 text-center">About NexCart</h1>
      
      <div className="prose prose-blue max-w-none space-y-8">
        <section>
          <h2 className="text-2xl font-semibold text-gray-800 mb-4">The Future of E-Commerce</h2>
          <p className="text-lg text-gray-600 leading-relaxed">
            NexCart is a state-of-the-art e-commerce platform designed for speed, security, and scalability. 
            Built using a modern microservices architecture, it leverages the power of Spring Boot on the backend 
             and Next.js on the frontend to provide a seamless shopping experience.
          </p>
        </section>

        <section className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
            <h3 className="text-xl font-bold text-blue-600 mb-3">Microservices Architecture</h3>
            <p className="text-gray-600 text-sm">
              Our system is decomposed into specialized services (User, Product, Order, Payment) 
              that communicate asynchronously, ensuring high availability and fault tolerance.
            </p>
          </div>
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
            <h3 className="text-xl font-bold text-blue-600 mb-3">Event-Driven Reliability</h3>
            <p className="text-gray-600 text-sm">
              Using the Saga pattern and Transactional Outbox, we guarantee data consistency 
              across distributed systems even during partial failures.
            </p>
          </div>
        </section>

        <section className="bg-blue-600 text-white p-8 rounded-2xl shadow-lg mt-12">
          <h2 className="text-2xl font-bold mb-4">Our Technology Stack</h2>
          <ul className="grid grid-cols-2 sm:grid-cols-3 gap-4 text-sm font-medium">
            <li className="flex items-center">✓ Spring Boot 3</li>
            <li className="flex items-center">✓ Next.js 15+</li>
            <li className="flex items-center">✓ Keycloak (IAM)</li>
            <li className="flex items-center">✓ Elasticsearch</li>
            <li className="flex items-center">✓ Kafka Messaging</li>
            <li className="flex items-center">✓ PostgreSQL</li>
          </ul>
        </section>

        <div className="text-center pt-12">
          <Link 
            href="/products" 
            className="inline-block bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
          >
            Start Shopping
          </Link>
        </div>
      </div>
    </div>
  );
}
