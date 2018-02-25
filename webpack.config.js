const webpack = require("webpack")
const path = require('path')
const ExtractTextPlugin = require('extract-text-webpack-plugin')
const dev = process.env.NODE_ENV === 'development';

module.exports = {
    entry: './ui/entry.js',
    output: {path: path.resolve(__dirname, 'public/compiled'), filename: 'bundle.js'},
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                loader: 'babel-loader',
                include: [
                    path.resolve(__dirname, 'ui')
                ],
                options: {presets: ['env', 'react']}
            },
            {
                test: /\.scss?$/,
                use:  ['style-loader', 'css-loader', 'sass-loader']
            },
        ],
    },
    plugins: [
    ],
    mode: dev ? 'development' : 'production'
}