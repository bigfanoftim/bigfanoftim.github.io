const { withContentlayer } = require("next-contentlayer");

const prefix = process.env.NODE_ENV === 'production' ? 'https://bigfanoftim.github.io/' : ''

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  output: "export",
  assetPrefix: prefix
};

module.exports = withContentlayer(nextConfig);
