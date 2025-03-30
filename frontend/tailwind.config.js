/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}", "*.{js,ts,jsx,tsx,mdx}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Noto Sans KR', 'sans-serif'],
      },
      colors: {
        primary: {
          50: '#fff1f1',
          100: '#ffe1e1',
          200: '#ffc7c7',
          300: '#ffa0a0',
          400: '#ff7a7a',
          500: '#ff5252',
          600: '#ff3838',
          700: '#ff1f1f',
          800: '#ff0f0f',
          900: '#ff0000',
        },
        secondary: {
          50: '#f5f7fa',
          100: '#ebeef5',
          200: '#d8deeb',
          300: '#b6c1d6',
          400: '#8c9cbc',
          500: '#6b7fa3',
          600: '#556789',
          700: '#445270',
          800: '#3a455d',
          900: '#333c4f',
        },
      },
      borderRadius: {
        'xl': '1rem',
        '2xl': '1.5rem',
      },
      boxShadow: {
        'soft': '0 4px 20px rgba(0, 0, 0, 0.05)',
        'card': '0 10px 30px rgba(0, 0, 0, 0.08)',
      },
    },
  },
  plugins: [],
}