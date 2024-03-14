const { withContentlayer } = require("next-contentlayer");

const isProd = process.env.NODE_ENV === 'production';

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  assetPrefix: isProd ? 'https://bigfanoftim.github.io/' : '',
  output: "export",
};

module.exports = withContentlayer(nextConfig);
